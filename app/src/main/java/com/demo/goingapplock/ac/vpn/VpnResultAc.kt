package com.demo.goingapplock.ac.vpn

import androidx.lifecycle.lifecycleScope
import com.demo.goingapplock.R
import com.demo.goingapplock.ad.AdSpace
import com.demo.goingapplock.ad.AdUtils
import com.demo.goingapplock.ad.RefreshAdManager
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.cache.MmkvData
import com.demo.goingapplock.interfaces.IConnectTimeInterface
import com.demo.goingapplock.isBuyUser
import com.demo.goingapplock.vpn.TimeManager
import kotlinx.android.synthetic.main.activity_vpn_home.*
import kotlinx.android.synthetic.main.activity_vpn_result.*
import kotlinx.android.synthetic.main.activity_vpn_result.ad_parent
import kotlinx.android.synthetic.main.activity_vpn_result.iv_back
import kotlinx.android.synthetic.main.activity_vpn_result.tv_vpn_name
import kotlinx.android.synthetic.main.activity_vpn_result.view_top
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VpnResultAc:BaseAc(), IConnectTimeInterface {
    private var connect=false

    override fun layoutId(): Int = R.layout.activity_vpn_result

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        connect=intent.getBooleanExtra("connect",false)
        iv_title.isSelected=connect
        iv_status.isSelected=connect
        tv_vpn_name.isSelected=connect
        tv_connect_time.isSelected=connect
        iv_back.setOnClickListener { onBackPressed() }
        if (connect){
            TimeManager.setInterface(this)
        }else{
            tv_connect_time.text=TimeManager.getTotalTimeStr()
        }
    }

    override fun connectTime(time: String) {
        tv_connect_time.text=time
    }

    override fun onResume() {
        super.onResume()
        if (RefreshAdManager.canRefresh(AdSpace.VPN_RESULT_BOTTOM.sName)){
            lifecycleScope.launch {
                delay(300)
                AdUtils.load(AdSpace.VPN_RESULT_BOTTOM)
                while (isResume) {
                    if (AdUtils.isHaveAd(AdSpace.VPN_RESULT_BOTTOM)){
                        val isShow= AdUtils.show(AdSpace.VPN_RESULT_BOTTOM,this@VpnResultAc, nativeAdParent = ad_parent)
                        if (isShow){
                            AdUtils.load(AdSpace.VPN_RESULT_BOTTOM)
                            RefreshAdManager.setValue(AdSpace.VPN_RESULT_BOTTOM.sName,false)
                            break
                        }
                    }
                    delay(300)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (MmkvData.readLocalReferrer().isBuyUser()&&AdUtils.isHaveAd(AdSpace.VPN_CONNECT)){
            if (!AdUtils.show(AdSpace.VPN_CONNECT,this, adClose = {
                    finish()
                })){
                finish()
            }
            return
        }else{
            AdUtils.load(AdSpace.VPN_CONNECT)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        TimeManager.setInterface(null)
        RefreshAdManager.setValue(AdSpace.VPN_RESULT_BOTTOM.sName,true)
    }
}