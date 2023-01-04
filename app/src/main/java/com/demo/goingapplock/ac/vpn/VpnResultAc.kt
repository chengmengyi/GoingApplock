package com.demo.goingapplock.ac.vpn

import com.demo.goingapplock.R
import com.demo.goingapplock.base.BaseAc
import kotlinx.android.synthetic.main.activity_vpn_result.*

class VpnResultAc:BaseAc(){
    private var connect=false

    override fun layoutId(): Int = R.layout.activity_vpn_result

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        connect=intent.getBooleanExtra("connect",false)
        iv_title.isSelected=connect
        iv_status.isSelected=connect
        tv_vpn_name.isSelected=connect
        tv_connect_time.isSelected=connect
        iv_back.setOnClickListener { finish() }

    }
}