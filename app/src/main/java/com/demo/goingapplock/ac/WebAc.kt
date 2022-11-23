package com.demo.goingapplock.ac

import com.demo.goingapplock.R
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.conf.GoingConf
import kotlinx.android.synthetic.main.activity_web.*

class WebAc:BaseAc() {
    override fun layoutId(): Int = R.layout.activity_web

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        iv_back.setOnClickListener { finish() }

        web_view.apply {
            settings.javaScriptEnabled=true
            loadUrl(GoingConf.URL)
        }
    }
}