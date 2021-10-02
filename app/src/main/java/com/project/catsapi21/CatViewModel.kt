package com.project.catsapi21

import androidx.lifecycle.ViewModel
import com.project.catsapi21.model.CatsList

class CatViewModel : ViewModel(){

    val getSavedItemList: ArrayList<CatsList> get() = savedItemList

    fun savedCatList(list: ArrayList<CatsList>?) {
        if (list != null) {
            savedItemList.addAll(list)
        }
    }
    companion object {
        var savedItemList = arrayListOf<CatsList>()
    }
}