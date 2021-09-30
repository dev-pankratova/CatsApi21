package com.project.catsapi21.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.project.catsapi21.CatsModelApi
import com.project.catsapi21.ServiceBuilder
import com.project.catsapi21.databinding.ContentListFragmentBinding
import com.project.catsapi21.model.CatsList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentListFragment : Fragment() {

    private var binding: ContentListFragmentBinding? = null

    private var PAGES = 1
    private val isLastPage = false
    private var isLoading = false
    private var mLayoutManager: GridLayoutManager? = null
    private var list: ArrayList<CatsList>? = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ContentListFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadMore()
        pagination()
    }

    private fun loadMore() {
        val request = ServiceBuilder.buildService(CatsModelApi::class.java)
        val call = request.loadCats("71e1b8baf781402aa67e4791daf5d432", PAGES, 20)
        call.enqueue(object : Callback<ArrayList<CatsList>> {
            override fun onResponse(
                call: Call<ArrayList<CatsList>>,
                response: Response<ArrayList<CatsList>>
            ) {
                if (response.isSuccessful) {
                    list = response.body()
                    getList(list)
                }
            }

            override fun onFailure(call: Call<ArrayList<CatsList>>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getList(list: ArrayList<CatsList>) {
        binding?.contentView?.adapter = CatAdapter(list, context!!)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    companion object {
        fun newInstance(): ContentListFragment {
            return ContentListFragment()
        }
    }
}