package com.project.catsapi21.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.project.catsapi21.R

class MainActivity : AppCompatActivity() {

    private var _contentFragment: ContentListFragment? = null
    private val contentFragment get() = _contentFragment

    //var currentFragment: Fragment? = null

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
        //TODO
    }

    fun showDetail(url: String) {
        frag2.setArguments(url)

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.card_flip_left_enter,
                R.anim.card_flip_left_exit
            )
            .replace(R.id.contentFragment, frag2)
            .addToBackStack(null)
            .commit()
    }

    private fun attachFragment(fragment: Fragment) {
        //currentFragment = fragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
        }
    }
}