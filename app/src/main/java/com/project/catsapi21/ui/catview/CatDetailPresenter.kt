package com.project.catsapi21.ui.catview

import java.text.SimpleDateFormat
import java.util.*

class CatDetailPresenter(private val view: CatDetailContract.View) : CatDetailContract.UserActionListener {
    private lateinit var url: String

    override fun setParams(url: String) {
        this.url = url
        view.showCat(url)
    }

    override fun onSaveClicked() {
        val date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val timeText = formatter.format(date)
        val fileName = "CatFrom$timeText.jpeg"
        view.saveCat(fileName)
    }

    override fun onBackClicked() {
        view.closeView()
    }
}
