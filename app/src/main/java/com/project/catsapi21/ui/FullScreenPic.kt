package com.project.catsapi21.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore.Images
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.project.catsapi21.R
import com.project.catsapi21.databinding.FullCatFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FullScreenPic : Fragment() {
    private var _binding: FullCatFragmentBinding? = null
    private val binding get() = _binding
    private var url: String? = null
    private var image: Bitmap? = null
    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initOptionsMenu()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FullCatFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCurrentFragment()
        getUrlFromArgument()
        setPicFromUrl()
        clickBackToList()
        clickSavePic()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            closeView()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initOptionsMenu() {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun getUrlFromArgument() {
        url = arguments?.getString(CAT_URL) ?: throw IllegalArgumentException("Not found url")
    }

    private fun setPicFromUrl() {
        url?.let { showCat(it) }
    }

    private fun clickBackToList() {
        binding?.btnBack?.setOnClickListener {
            closeView()
        }
    }

    private fun clickSavePic() {
        binding?.btnSave?.setOnClickListener {
            generateFileName()

            CoroutineScope(Dispatchers.IO).launch {
                image = getCatImage()
                saveToGallery(image)
            }
        }
    }

    private fun generateFileName() {
        val date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val timeText = formatter.format(date)
        fileName = "Cat_$timeText"
    }

    private fun getCatImage(): Bitmap {
        return Glide.with(requireContext())
            .asBitmap()
            .load(url)
            .submit()
            .get()
    }

    private fun saveToGallery(image: Bitmap?) {
        if (checkStoragePermission()) {
            Images.Media.insertImage(activity?.contentResolver, image, fileName, "description")
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Picture is saved in gallery", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestStoragePermission()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_STORAGE_PERMISSION
        )
    }

    private fun setCurrentFragment() {
        (activity as MainActivity).currentFragment = (activity as MainActivity).fullPicFragment
    }

    private fun showCat(url: String) {
        val context = context ?: return
        binding?.catImageView?.let {
            Glide.with(context)
                .load(url)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding?.progressFullCat?.visibility = View.GONE
                        binding?.catImageView?.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding?.progressFullCat?.visibility = View.GONE
                        binding?.catImageView?.visibility = View.VISIBLE
                        return false
                    }
                })
                .centerCrop()
                .error(R.drawable.ic_launcher_background)
                .into(it)
        }
    }

    private fun closeView() {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (context as MainActivity).onBackPressed()
    }

    companion object {
        fun newInstance(url: String): FullScreenPic {
            val fragment = FullScreenPic()
            val args = Bundle()
            args.putString(CAT_URL, url)
            fragment.arguments = args
            return fragment
        }

        private const val CAT_URL = "CAT_URL"
        private var REQUEST_STORAGE_PERMISSION = 122
    }
}