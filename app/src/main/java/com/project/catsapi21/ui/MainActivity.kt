package com.project.catsapi21.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.project.catsapi21.CatViewModel
import com.project.catsapi21.R
import com.project.catsapi21.listeners.OnSendClickDataToActivity

class MainActivity : AppCompatActivity() {

    private val viewModel: CatViewModel by viewModels()

    private var _contentFragment: ContentListFragment? = null
    val contentFragment get() = _contentFragment

    private var _fullPicFragment: FullScreenPic? = null
    val fullPicFragment get() = _fullPicFragment

    var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val restoreFullPicFragment = supportFragmentManager.findFragmentByTag(FULL_PIC_TAG)
        val restoreContentFragment = supportFragmentManager.findFragmentByTag(CONTENT_TAG)
        if (restoreFullPicFragment != null) {
            restoreContentFragment?.let { attachFragment(it, CONTENT_TAG) }
            attachFragment(restoreFullPicFragment, FULL_PIC_TAG)
            viewModel.saveCurrentFragment(restoreFullPicFragment)
        } else {
            contentFragment?.let { viewModel.saveCurrentFragment(it) }
            showContentFragment()
        }
        openFullScreenPic()
    }

    private fun showContentFragment() {
        _contentFragment = ContentListFragment.newInstance()
        contentFragment?.let { attachFragment(it, CONTENT_TAG) }
    }

    private fun openFullScreenPic() {
        contentFragment?.sendDataToActivity(object : OnSendClickDataToActivity {
            override fun sendUrlData(url: String?) {
                _fullPicFragment = url?.let { FullScreenPic.newInstance(it) }
                fullPicFragment?.let { attachFragmentWithAnimation(it) }
            }
        })
    }

    private fun attachFragment(fragment: Fragment, tag: String) {
        currentFragment = fragment
        currentFragment?.let { viewModel.saveCurrentFragment(it) }
        viewModel.saveFragmentManager(supportFragmentManager)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, tag)
            .commit()
    }

    private fun attachFragmentWithAnimation(fragment: Fragment) {
        currentFragment = fragment
        currentFragment?.let { viewModel.saveCurrentFragment(it) }
        if (supportFragmentManager.isDestroyed) {
            val f = viewModel.getFragmentManager
            f?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.card_flip_left_enter,
                    R.anim.card_flip_left_exit
                )
                ?.replace(R.id.container, fragment, FULL_PIC_TAG)
                ?.addToBackStack(null)
                ?.commit()
        } else {
            viewModel.saveFragmentManager(supportFragmentManager)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.card_flip_left_enter,
                    R.anim.card_flip_left_exit
                )
                .replace(R.id.container, fragment, FULL_PIC_TAG)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onBackPressed() {
        val curFragment = viewModel.getCurrentFragment
        if (curFragment != null || contentFragment != null) {
            if (curFragment == contentFragment) {
                viewModel.saveCatParcelable(null)
                viewModel.clearCatList()
                finishAndRemoveTask()
            }
            else super.onBackPressed()
        } else super.onBackPressed()
    }

    private companion object {
        private const val FULL_PIC_TAG = "fullPic_tag"
        private const val CONTENT_TAG = "content_tag"
    }
}
