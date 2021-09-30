package com.project.catsapi21.ui.catview

import android.os.Bundle
import androidx.fragment.app.Fragment

class FullScreenPic : Fragment() {
    private lateinit var presenter: CatDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = CatDetailPresenter(this)

    }


}