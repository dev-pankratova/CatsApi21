package com.project.catsapi21.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.project.catsapi21.databinding.FullCatFragmentBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FullScreenPic : Fragment() {
    private var binding: FullCatFragmentBinding? = null
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initOptionsMenu()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FullCatFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCurrentFragment()
        getUrlFromArgument()
        setPicFromUrl()
        clickBackToList()
        clickSavePic()
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
        url = arguments?.getString(CAT_URL)?: throw IllegalArgumentException("Not found url")
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
            val date = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            val timeText = formatter.format(date)
            val fileName = "CatFrom$timeText.jpeg"
            saveCat(fileName)
        }
    }

    private fun setCurrentFragment()  {
        (activity as MainActivity).currentFragment = (activity as MainActivity).fullPicFragment
    }

    private fun showCat(url: String) {
        val context = context ?: return
        binding?.catImageView?.let {
            Glide.with(context)
                .load(url)
                .centerCrop()
                .into(it)
        }
    }

    private fun saveCat(nameImage: String) {
        val storageDir = File(context?.getExternalFilesDir(null)?.absolutePath!!)
        //val storageDir = File(context?.cacheDir)
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, nameImage)
            //TODO переделать try
            try {
                val outputStream = FileOutputStream(imageFile)
                val img = binding?.catImageView?.drawable as BitmapDrawable
                img.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                Toast.makeText(context, "This image is saved in storage", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Image is not saved. Please try again", Toast.LENGTH_SHORT).show()
            }
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
    }
}