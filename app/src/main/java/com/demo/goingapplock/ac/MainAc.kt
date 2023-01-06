package com.demo.goingapplock.ac

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.demo.goingapplock.R
import com.demo.goingapplock.ac.vpn.VpnHomeAc
import com.demo.goingapplock.ad.AdSpace
import com.demo.goingapplock.ad.AdUtils
import com.demo.goingapplock.ad.RefreshAdManager
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.cache.GoingCache
import com.demo.goingapplock.cache.MmkvData
import com.demo.goingapplock.conf.GoingConf
import com.demo.goingapplock.dialog.VpnDialog
import com.demo.goingapplock.isBuyUser
import com.demo.goingapplock.manager.PointManager
import com.demo.goingapplock.vpn.ConnectManager
import kotlinx.android.synthetic.main.activity_main.*

class MainAc : BaseAc() {
    private var progress: ValueAnimator? = null

    override fun layoutId(): Int = R.layout.activity_main

    override fun onView() {
        loadAd()
        RefreshAdManager.resetMap()
        startProgress()
    }

    private fun startProgress() {
        var isShowAd = false
        progress = ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                pb_progress.progress = progress
                tv_progress.text = "$progress%"
                if (AdUtils.isHaveAd(AdSpace.OPEN)) {
                    isShowAd = showAd()
                }
            }
            doOnEnd {
                if (!isShowAd)
                    checkPlanType()
            }
            start()
        }
    }

    private fun checkPlanType(){
        if (MmkvData.isFirstLoad()){
            MmkvData.setFirstLoad()
            doPlanB()
            return
        }
        if (!MmkvData.readLocalReferrer().isBuyUser()){
            PointManager.setUser("a")
            doPlanA()
            return
        }
        MmkvData.randomPlanType()
        PointManager.setUser(GoingConf.planType.toLowerCase())
        if (ConnectManager.connected()){
            toHomeAc()
        }else{
            if (GoingConf.planType=="A"){
                doPlanA()
            }else{
                doPlanB()
            }
        }
    }

    private fun doPlanA(){
        val isHot = intent.getBooleanExtra("isHot", false)
        if (GoingConf.gawa_vpn_pop=="1"&&canShowVpnDialog()&&ConnectManager.disconnected()){
            showVpnDialog()
            return
        }
        if (GoingConf.gawa_vpn_pop=="2"&&canShowVpnDialog()&&!isHot&&ConnectManager.disconnected()){
            showVpnDialog()
            return
        }
        toHomeAc()
    }

    private fun doPlanB(){
        startActivity(Intent(this,VpnHomeAc::class.java).apply {
            putExtra("auto",true)
        })
        finish()
    }

    private fun canShowVpnDialog():Boolean{
        when(GoingConf.gawa_re){
            "1"->return true
            "2"->return MmkvData.readLocalReferrer().isBuyUser()
            "3"->{
                val readLocalReferrer = MmkvData.readLocalReferrer()
                return readLocalReferrer.contains("facebook")||readLocalReferrer.contains("fb4a")
            }
        }
        return false
    }

    private fun showVpnDialog(){
        VpnDialog{
            if (it){
                doPlanB()
            }else{
                toHomeAc()
            }
        }.show(supportFragmentManager,"VpnDialog")
    }

    private fun toHomeAc() {
        startActivity(Intent(this, HomeAc::class.java))
        finish()
    }

    private fun stopAc() {
        progress?.removeAllUpdateListeners()
        progress?.cancel()
        progress = null
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        progress?.resume()
    }

    override fun onPause() {
        super.onPause()
        progress?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAc()
    }

    private fun loadAd() {
        GoingCache.refreshLimitData()
        AdUtils.load(AdSpace.OPEN, retryNum = 1)
        AdUtils.load(AdSpace.HOME_NT)
        AdUtils.load(AdSpace.WIFI_CLICK)
        AdUtils.load(AdSpace.RETURN_I)
        AdUtils.load(AdSpace.PASS_ENTER)
        AdUtils.load(AdSpace.VPN_HOME)
        AdUtils.load(AdSpace.VPN_CONNECT)
        AdUtils.load(AdSpace.VPN_RESULT_BOTTOM)
        AdUtils.load(AdSpace.VPN_SERVER_BOTTOM)
    }

    private fun showAd(): Boolean {
        return AdUtils.show(AdSpace.OPEN, this, adClose = {
            checkPlanType()
        })
    }
}