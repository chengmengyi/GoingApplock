package com.demo.goingapplock.ac.wifi

import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.goingapplock.R
import com.demo.goingapplock.ac.vpn.VpnHomeAc
import com.demo.goingapplock.ad.AdSpace
import com.demo.goingapplock.ad.AdUtils
import com.demo.goingapplock.adapter.WifiResultInfoAdapter
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.bean.WifiInfoBean
import com.demo.goingapplock.vpn.ConnectManager
import kotlinx.android.synthetic.main.activity_wifi_scan_result.*

class WifiScanResultAc:BaseAc() {
    private val list= arrayListOf<WifiInfoBean>()
    private val wifiResultInfoAdapter by lazy { WifiResultInfoAdapter(this,list) }

    override fun layoutId(): Int = R.layout.activity_wifi_scan_result

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        iv_back.setOnClickListener { onBackPressed() }
        tv_scan.setOnClickListener {
            startActivity(Intent(this,WifiScanAc::class.java))
            finish()
        }
        setInfo()
        setAdapter()
        AdUtils.load(AdSpace.RETURN_I)
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

    override fun onResume() {
        super.onResume()
        if (!ConnectManager.connected()){
            tv_to_vpn.text="no vpn connection"
            tv_to_vpn.setOnClickListener {
                startActivity(Intent(this, VpnHomeAc::class.java))
            }
        }else{
            tv_to_vpn.text="with vpn connection"
        }
    }

    private fun setAdapter(){
        rv_wifi_result.apply {
            layoutManager=LinearLayoutManager(this@WifiScanResultAc)
            adapter=wifiResultInfoAdapter
        }
    }

    override fun onBackPressed() {
        if (!AdUtils.show(AdSpace.RETURN_I,this, adClose = {
                AdUtils.load(AdSpace.RETURN_I)
                finish()
            })){
            super.onBackPressed()
        }
    }
}