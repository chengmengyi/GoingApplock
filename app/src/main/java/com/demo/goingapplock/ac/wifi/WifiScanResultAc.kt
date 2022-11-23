package com.demo.goingapplock.ac.wifi

import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.goingapplock.R
import com.demo.goingapplock.adapter.WifiResultInfoAdapter
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.bean.WifiInfoBean
import kotlinx.android.synthetic.main.activity_wifi_scan_result.*

class WifiScanResultAc:BaseAc() {
    private val list= arrayListOf<WifiInfoBean>()
    private val wifiResultInfoAdapter by lazy { WifiResultInfoAdapter(this,list) }

    override fun layoutId(): Int = R.layout.activity_wifi_scan_result

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        iv_back.setOnClickListener { finish() }
        tv_scan.setOnClickListener {
            startActivity(Intent(this,WifiScanAc::class.java))
            finish()
        }
        setInfo()
        setAdapter()
    }

    private fun setInfo(){
        val hasPwd = intent.getBooleanExtra("hasPwd", false)
        if(hasPwd){
            tv_scan.setBackgroundResource(R.drawable.bg_scan_has_pwd)
            iv_wifi_result.setImageResource(R.drawable.wifi_result2)
            tv_wifi_result.text="Private network such as home \nOr work network"
        }else{
            tv_scan.setBackgroundResource(R.drawable.bg_scan_no_pwd)
            iv_wifi_result.setImageResource(R.drawable.wifi_result1)
            tv_wifi_result.text="Public network with little \nOr no security"
        }
        list.addAll(intent.getSerializableExtra("list") as ArrayList<WifiInfoBean>)
    }

    private fun setAdapter(){
        rv_wifi_result.apply {
            layoutManager=LinearLayoutManager(this@WifiScanResultAc)
            adapter=wifiResultInfoAdapter
        }
    }
}