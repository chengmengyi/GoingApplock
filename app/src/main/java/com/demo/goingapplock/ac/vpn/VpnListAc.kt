package com.demo.goingapplock.ac.vpn

import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.goingapplock.R
import com.demo.goingapplock.adapter.VpnListAdapter
import com.demo.goingapplock.base.BaseAc
import kotlinx.android.synthetic.main.activity_vpn_list.*

class VpnListAc: BaseAc(){
    private val vpnAdapter by lazy { VpnListAdapter(this) }

    override fun layoutId(): Int = R.layout.activity_vpn_list

    override fun onView() {
        rv_list.apply {
            layoutManager=LinearLayoutManager(this@VpnListAc)
            adapter=vpnAdapter
        }
    }
}