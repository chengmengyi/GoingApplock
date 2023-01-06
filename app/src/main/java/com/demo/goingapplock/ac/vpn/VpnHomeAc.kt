package com.demo.goingapplock.ac.vpn

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.net.VpnService
import android.view.animation.LinearInterpolator
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ActivityUtils
import com.demo.goingapplock.GoingApp
import com.demo.goingapplock.GoingApp.Companion.isIRUser
import com.demo.goingapplock.R
import com.demo.goingapplock.ac.HomeAc
import com.demo.goingapplock.ad.AdSpace
import com.demo.goingapplock.ad.AdUtils
import com.demo.goingapplock.ad.RefreshAdManager
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.conf.GoingConf
import com.demo.goingapplock.dialog.IRNoticeDialog
import com.demo.goingapplock.dialog.NoticeDialog
import com.demo.goingapplock.getVpnLogo
import com.demo.goingapplock.interfaces.IConnectInterface
import com.demo.goingapplock.manager.PointManager
import com.demo.goingapplock.manager.show
import com.demo.goingapplock.manager.toast
import com.demo.goingapplock.netStatus
import com.demo.goingapplock.vpn.ConnectManager
import com.demo.goingapplock.vpn.VpnInfoManager
import com.github.shadowsocks.utils.StartService
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.android.synthetic.main.activity_home_content.*
import kotlinx.android.synthetic.main.activity_vpn_home.*
import kotlinx.android.synthetic.main.activity_vpn_home.ad_parent
import kotlinx.android.synthetic.main.activity_vpn_home.view_top
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VpnHomeAc:BaseAc(), IConnectInterface {
    private var canClick=true
    private var permission=false
    private var connect=true
    private var autoConnect=false
    private var objectAnimator:ObjectAnimator?=null
    private var valueAnimator:ValueAnimator?=null
    private val requestPermission = registerForActivityResult(StartService()){
        if (!it&&permission){
            PointManager.point("gawa_auth")
            permission=false
            startConnectVpn()
        }else{
            canClick=true
            PointManager.point("gawa_vpn_fail")
            toast("Connect Fail")
        }
    }

    override fun layoutId(): Int = R.layout.activity_vpn_home

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        if (isIRUser){
            IRNoticeDialog{
                onBackPressed()
            }.show(supportFragmentManager,"IRNoticeDialog")
            return
        }

        ConnectManager.onCreate(this,this)
        setVpnInfo()
        if (ConnectManager.connected()){
            setConnectedInfo()
        }
        iv_connect_center.setOnClickListener {
            clickConnectBtn()
        }
        llc_vpn_info.setOnClickListener {
            if (canClick){
                startActivityForResult(Intent(this,VpnListAc::class.java),15)
            }
        }
        iv_back.setOnClickListener {
            onBackPressed()
        }

        autoConnect=intent.getBooleanExtra("auto",false)
        if (autoConnect){
            clickConnectBtn()
        }
    }

    private fun clickConnectBtn(){
        if (isIRUser) return
        AdUtils.load(AdSpace.VPN_CONNECT)
        AdUtils.load(AdSpace.VPN_RESULT_BOTTOM)
        if (!canClick)return
        if (!autoConnect){
            PointManager.point("gawa_vpn_function")
        }
        canClick=false
        val connected = ConnectManager.connected()
        if (connected){
            setStoppingInfo()
            ConnectManager.disconnectVpn()
            startPercentAnimator(false)
        }else{
            if (netStatus()==1){
                PointManager.point("gawa_vpn_fail")
                NoticeDialog("You are not currently connected to the network"){}.show(supportFragmentManager,"NoticeDialog")
                canClick=true
                return
            }
            if (VpnService.prepare(this)!=null){
                permission=true
                requestPermission.launch(null)
                return
            }
            startConnectVpn()
        }
    }

    private fun startConnectVpn(){
        PointManager.point("gawa_vpn_connect")
        setConnectingInfo()
        ConnectManager.connectVpn()
        startPercentAnimator(true)
    }

    private fun startPercentAnimator(connect:Boolean){
        this.connect=connect
        valueAnimator=ValueAnimator.ofInt(0,100).apply {
            duration=10000L
            interpolator=LinearInterpolator()
            addUpdateListener {
                val i = it.animatedValue as Int
                val percent = (10 * (i / 100.0F)).toInt()
                tv_connect_percent.text="$i%"
                if (percent in 3 .. 9){
                    val ad = AdSpace.VPN_CONNECT.container.ad
                    if (null!=ad&&ad is InterstitialAd){
                        if (!AdUtils.show(AdSpace.VPN_CONNECT,this@VpnHomeAc, adClose = {
                                checkConnectResult()
                        })){
                            endPercentAnimator()
                            endObjectAnimator()
                            checkConnectResult()
                        }else{
                            endPercentAnimator()
                            endObjectAnimator()
                        }
                    }

                }else if (percent>=10){
                    endPercentAnimator()
                    endObjectAnimator()
                    checkConnectResult()
                }
            }
            start()
        }
    }

    private fun checkConnectResult(jump:Boolean=true){
        val b = if (connect) ConnectManager.connected() else ConnectManager.disconnected()
        if (b){
            if (connect){
                PointManager.point("gawa_vpn_succ")
                if (GoingConf.gawa_ab=="B"&&autoConnect){
                    AdUtils.removeAllAdCache()
                }
                setConnectedInfo()
            }else{
                setVpnInfo()
                setStoppedInfo()
            }
            if (jump){
                jumpToResultAc()
            }
        }else{
            if (connect){
                PointManager.point("gawa_vpn_fail")
            }
            setStoppedInfo()
            toast(if (connect)"Connect Fail" else "Disconnect Fail")
        }
        canClick=true
    }

    private fun jumpToResultAc(){
        if (GoingApp.appFront){
            startActivity(Intent(this,VpnResultAc::class.java).apply {
                putExtra("connect",connect)
            })
        }
    }

    private fun endPercentAnimator(){
        valueAnimator?.removeAllUpdateListeners()
        valueAnimator?.cancel()
        valueAnimator=null
    }

    private fun setVpnInfo(){
        val currentVpn = ConnectManager.currentBean
        tv_vpn_name.text=currentVpn.gawa_s_coun
        iv_vpn_logo.setImageResource(getVpnLogo(currentVpn.gawa_s_coun))
    }

    private fun setConnectingInfo(){
        iv_connect_center.setImageResource(R.drawable.home7)
        startObjectAnimator()
        iv_connect_status.show(false)
        tv_connect_percent.show(true)
        tv_click_tips.show(false)
        tv_connect_status.text="Status: Connecting"
    }

    private fun setConnectedInfo(){
        iv_connect_center.setImageResource(R.drawable.home8)
        endObjectAnimator()
        iv_connect_status.show(false)
        tv_connect_percent.show(false)
        tv_click_tips.show(false)
        tv_connect_status.text="Status: Connected"
    }
    private fun setStoppingInfo(){
        iv_connect_center.setImageResource(R.drawable.home7)
        startObjectAnimator()
        iv_connect_status.show(false)
        tv_connect_percent.show(true)
        tv_click_tips.show(false)
        tv_connect_status.text="Status: Stopping"
    }

    private fun setStoppedInfo(){
        iv_connect_center.setImageResource(R.drawable.home6)
        endObjectAnimator()
        iv_connect_status.show(true)
        tv_connect_percent.show(false)
        tv_click_tips.show(true)
        tv_connect_status.text="Status: Disconnected"
    }

    private fun startObjectAnimator(){
        objectAnimator=ObjectAnimator.ofFloat(iv_connect_center,"rotation",0f,360f).apply {
            duration=1000L
            repeatCount=ValueAnimator.INFINITE
            repeatMode=ObjectAnimator.RESTART
            start()
        }
    }

    private fun endObjectAnimator(){
        iv_connect_center.rotation=0F
        objectAnimator?.removeAllUpdateListeners()
        objectAnimator?.cancel()
        objectAnimator=null
    }

    override fun connectSuccess() {
        setConnectedInfo()
    }

    override fun disconnectSuccess() {
        if (canClick){
            setStoppedInfo()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==15){
            when(data?.getStringExtra("result")){
                "disconnect"->{
                    iv_connect_center.performClick()
                }
                "connect"->{
                    setVpnInfo()
                    iv_connect_center.performClick()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (RefreshAdManager.canRefresh(AdSpace.VPN_HOME.sName)){
            lifecycleScope.launch {
                delay(300)
                AdUtils.load(AdSpace.VPN_HOME)
                while (isResume) {
                    if (AdUtils.isHaveAd(AdSpace.VPN_HOME)){
                        val isShow= AdUtils.show(AdSpace.VPN_HOME,this@VpnHomeAc, nativeAdParent = ad_parent)
                        if (isShow){
                            AdUtils.load(AdSpace.VPN_HOME)
                            RefreshAdManager.setValue(AdSpace.VPN_HOME.sName,false)
                            break
                        }
                    }
                    delay(300)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!canClick) return
        if (!ActivityUtils.isActivityExistsInStack(HomeAc::class.java)){
            startActivity(Intent(this,HomeAc::class.java))
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        endObjectAnimator()
        endPercentAnimator()
        ConnectManager.onDestroy()
        RefreshAdManager.setValue(AdSpace.VPN_HOME.sName,true)
    }
}