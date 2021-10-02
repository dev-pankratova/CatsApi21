package com.project.catsapi21.ui

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.catsapi21.CatViewModel
import com.project.catsapi21.CatsModelApi
import com.project.catsapi21.PaginationScrollListener
import com.project.catsapi21.ServiceBuilder
import com.project.catsapi21.databinding.ContentListFragmentBinding
import com.project.catsapi21.listeners.OnItemClickListener
import com.project.catsapi21.listeners.OnSendClickDataToActivity
import com.project.catsapi21.model.CatsList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentListFragment : Fragment() {

    private var binding: ContentListFragmentBinding? = null
    private var sendDataInterface: OnSendClickDataToActivity? = null
    private val viewModel: CatViewModel by viewModels()

    private var PAGES = 1
    private val isLastPage = false
    private var isLoading = false
    private var mLayoutManager: GridLayoutManager? = null
    private var list: ArrayList<CatsList>? = arrayListOf()
    private var catsAdapter: CatAdapter? = null
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View?
        if (savedInstanceState != null) {
            val savedRecyclerLayoutState: Parcelable? = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT)
            binding?.contentView?.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutState)
            view = binding?.root
        } else {
            binding = ContentListFragmentBinding.inflate(inflater, container, false)
            view = binding?.root
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.contentView?.layoutManager = createLayoutManager()
        setSwipeListener()
        loadMore()
        pagination()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).currentFragment = (activity as MainActivity).contentFragment
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val savedRecyclerLayoutManager: Parcelable? = savedInstanceState.getParcelable(
                BUNDLE_RECYCLER_LAYOUT)
            binding?.contentView?.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutManager)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, binding?.contentView?.layoutManager?.onSaveInstanceState())
    }

    private fun setSwipeListener() {
        binding?.refreshLayout?.setOnRefreshListener {
            runnable = Runnable {
                loadMore()
                binding?.refreshLayout?.isRefreshing = false
            }
        }
    }

    private fun createLayoutManager(): GridLayoutManager {
        val grid = GridLayoutManager(context, 3)
        mLayoutManager = grid
        return grid
    }

    private fun loadMore() {
        val savedList = viewModel.getSavedItemList
        if (savedList.isNullOrEmpty()) {
            val request = ServiceBuilder.buildService(CatsModelApi::class.java)
            val call = request.loadCats("71e1b8baf781402aa67e4791daf5d432", PAGES, 25)
            call.enqueue(object : Callback<ArrayList<CatsList>> {
                override fun onResponse(
                    call: Call<ArrayList<CatsList>>,
                    response: Response<ArrayList<CatsList>>
                ) {
                    if (response.isSuccessful) {
                        list = response.body()
                        viewModel.savedCatList(list)
                        list?.let { getList(it)
                            setItemClickListener()}
                    }
                }

                override fun onFailure(call: Call<ArrayList<CatsList>>, t: Throwable) {
                    Toast.makeText(context, "Включи интернет!!!!", Toast.LENGTH_SHORT).show()
                    //Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            getList(savedList)
            setItemClickListener()
        }
    }

    private fun loadMore2() {
        val request = ServiceBuilder.buildService(CatsModelApi::class.java)
        val call = request.loadCats("71e1b8baf781402aa67e4791daf5d432", PAGES++, 25)
        call.enqueue(object : Callback<ArrayList<CatsList>> {
            override fun onResponse(
                call: Call<ArrayList<CatsList>>,
                response: Response<ArrayList<CatsList>>
            ) {
                if (response.isSuccessful) {
                    isLoading = false
                    list = response.body()
                    viewModel.savedCatList(list)
                    list?.let { (binding?.contentView?.adapter as CatAdapter).addData(it) }
                }
            }

            override fun onFailure(call: Call<ArrayList<CatsList>>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getList(list: ArrayList<CatsList>) {
        catsAdapter = CatAdapter(list, requireContext())
        binding?.contentView?.adapter = catsAdapter
    }

    private fun pagination() {
        binding?.contentView?.addOnScrollListener(object : PaginationScrollListener(mLayoutManager!!){
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                Toast.makeText(context, "Paging start", Toast.LENGTH_SHORT).show()
                loadMore2()
            }
        })
    }

    private fun setItemClickListener() {
        catsAdapter?.setListener(object : OnItemClickListener {
            override fun onCatClick(url: String?) {
                if (url != null) {
                    sendDataInterface?.sendUrlData(url)
                }
            }
        })
    }

    fun sendDataToActivity(inter: OnSendClickDataToActivity) {
        this.sendDataInterface = inter
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    companion object {
        fun newInstance(): ContentListFragment {
            return ContentListFragment()
        }
        private const val BUNDLE_RECYCLER_LAYOUT = "recycler.layout"
    }
}