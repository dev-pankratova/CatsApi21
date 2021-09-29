package com.project.catsapi21.ui.catview

interface CatDetailContract {

    interface View {

        fun showCat(url: String)

        fun saveCat(nameImage: String)

        fun closeView()
    }

    interface UserActionListener {

        fun setParams(url: String)

        fun onSaveClicked()

        fun onBackClicked()
    }
}
