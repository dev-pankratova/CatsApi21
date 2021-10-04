package com.project.catsapi21
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.project.catsapi21.model.CatsList

class CatViewModel : ViewModel() {

    val getSavedItemList: ArrayList<CatsList> get() = savedItemList

    fun savedCatList(list: ArrayList<CatsList>?) {
        if (list != null) {
            savedItemList.addAll(list)
        }
    }

    fun clearCatList() {
        savedItemList.clear()
    }

    val getCurrentFragment: Fragment? get() = currentFragment

    fun saveCurrentFragment(fragment: Fragment) {
        currentFragment = fragment
    }

    val getFragmentManager: FragmentManager? get() = fManager

    fun saveFragmentManager(fragmentM: FragmentManager) {
        fManager = fragmentM
    }

    val getCatParcelable: Parcelable? get() = catParcelable

    fun saveCatParcelable(parcelableCat: Parcelable?) {
        catParcelable = parcelableCat
    }

    companion object {
        private var savedItemList = arrayListOf<CatsList>()
        private var currentFragment: Fragment? = null
        private var fManager: FragmentManager? = null
        private var catParcelable: Parcelable? = null
    }
}
