package com.project.catsapi21.ui
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
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

    private var _binding: ContentListFragmentBinding? = null
    private val binding get() = _binding
    private val viewModel: CatViewModel by viewModels()

    private var pages = 1
    private val isLastPage = false
    private var isLoading = false
    private var mLayoutManager: GridLayoutManager? = null
    private var list: ArrayList<CatsList>? = arrayListOf()
    private var catsAdapter: CatAdapter? = null
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View?
        if (savedInstanceState != null) {
            val savedRecyclerLayoutState: Parcelable? = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT)
            _binding?.contentView?.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutState)
            view = binding?.root
        } else {
            _binding = ContentListFragmentBinding.inflate(inflater, container, false)
            view = binding?.root
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.contentView?.layoutManager = createLayoutManager()
        initOptionsMenu()
        setSwipeListener()
        loadMore()
        pagination()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).currentFragment = (activity as MainActivity).contentFragment
        (activity as MainActivity).contentFragment?.let { viewModel.saveCurrentFragment(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.contentView?.layoutManager?.onSaveInstanceState()?.let {
            viewModel.saveCatParcelable(
                it
            )
        }
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, binding?.contentView?.layoutManager?.onSaveInstanceState())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val savedRecyclerLayoutManager: Parcelable? = savedInstanceState.getParcelable(
                BUNDLE_RECYCLER_LAYOUT)
            binding?.contentView?.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutManager)
        } else {
            val getParcelable = viewModel.getCatParcelable
            if (getParcelable != null) {
                binding?.contentView?.layoutManager?.onRestoreInstanceState(getParcelable)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initOptionsMenu() {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun setSwipeListener() {
        handler = Handler()
        binding?.refreshLayout?.setOnRefreshListener {
            runnable = Runnable {
                loadMore()
                binding?.refreshLayout?.isRefreshing = false
            }

            handler.postDelayed(
                runnable, SPINNER_DURATION.toLong()
            )
        }
    }

    private fun createLayoutManager(): GridLayoutManager {
        val grid = GridLayoutManager(context, COLUMN_COUNT)
        mLayoutManager = grid
        return grid
    }

    private fun loadMore() {
        val savedList = viewModel.getSavedItemList
        if (savedList.isNullOrEmpty()) {
            val request = ServiceBuilder.buildService(CatsModelApi::class.java)
            val call = request.loadCats("71e1b8baf781402aa67e4791daf5d432", pages, PAGES_COUNT)
            call.enqueue(object : Callback<ArrayList<CatsList>> {
                override fun onResponse(
                    call: Call<ArrayList<CatsList>>,
                    response: Response<ArrayList<CatsList>>
                ) {
                    if (response.isSuccessful) {
                        list = response.body()
                        viewModel.savedCatList(list)
                        list?.let { getList(it)
                            setItemClickListener() }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CatsList>>, t: Throwable) {
                    Toast.makeText(context, "Включи интернет и свайпни!!!!", Toast.LENGTH_SHORT).show()
                    // Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            getList(savedList)
            setItemClickListener()
        }
    }

    private fun loadMore2() {
        val request = ServiceBuilder.buildService(CatsModelApi::class.java)
        val call = request.loadCats("71e1b8baf781402aa67e4791daf5d432", pages++, PAGES_COUNT)
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
        binding?.contentView?.addOnScrollListener(object : PaginationScrollListener(mLayoutManager!!) {
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

    val getInterface get() = sendDataInterface

    fun sendDataToActivity(inter: OnSendClickDataToActivity) {
        sendDataInterface = inter
    }

    companion object {
        fun newInstance(): ContentListFragment {
            return ContentListFragment()
        }
        private var sendDataInterface: OnSendClickDataToActivity? = null
        private const val BUNDLE_RECYCLER_LAYOUT = "recycler.layout"
        private const val SPINNER_DURATION = 3000
        private const val COLUMN_COUNT = 3
        private const val PAGES_COUNT = 25
    }
}
