package com.project.catsapi21.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.project.catsapi21.R
import com.project.catsapi21.listeners.OnSendClickDataToActivity

class MainActivity : AppCompatActivity() {

    private var _contentFragment: ContentListFragment? = null
    val contentFragment get() = _contentFragment

    private var _fullPicFragment: FullScreenPic? = null
    val fullPicFragment get() = _fullPicFragment

    var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showContentFragment()
        openFullScreenPic()
    }

    private fun showContentFragment() {
        _contentFragment = ContentListFragment.newInstance()
        contentFragment?.let { attachFragment(it) }
    }

    private fun openFullScreenPic() {
        contentFragment?.sendDataToActivity(object : OnSendClickDataToActivity {
            override fun sendUrlData(url: String?) {
                _fullPicFragment = url?.let { FullScreenPic.newInstance(it) }
                fullPicFragment?.let { attachFragmentWithAnimation(it) }
            }
        })
    }

    private fun attachFragment(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            //.addToBackStack(null)
            .commit()

    }

    private fun attachFragmentWithAnimation(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.card_flip_left_enter,
                R.anim.card_flip_left_exit
            )
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()

    }

    override fun onBackPressed() {
        if (currentFragment == contentFragment) finishAndRemoveTask()
        else super.onBackPressed()
    }
}