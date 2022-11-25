package com.demo.goingapplock.ac

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ActivityUtils
import com.demo.goingapplock.R
import com.demo.goingapplock.ad.AdSpace
import com.demo.goingapplock.ad.AdUtils
import com.demo.goingapplock.base.BaseAc
import kotlinx.android.synthetic.main.activity_main.*

class MainAc : BaseAc() {
    private var progress: ValueAnimator? = null

    override fun layoutId(): Int = R.layout.activity_main

    override fun onView() {
        loadAd()
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
                    toHomeAc()
            }
            start()
        }
    }

    private fun toHomeAc() {
        val activityExistsInStack = ActivityUtils.isActivityExistsInStack(HomeAc::class.java)
        if (!activityExistsInStack) {
            startActivity(Intent(this, HomeAc::class.java))
        }
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
        AdUtils.load(AdSpace.OPEN, retryNum = 1)
        AdUtils.load(AdSpace.HOME_NT)
        AdUtils.load(AdSpace.WIFI_CLICK)
        AdUtils.load(AdSpace.RETURN_I)
        AdUtils.load(AdSpace.PASS_ENTER)
    }

    private fun showAd(): Boolean {
        return AdUtils.show(AdSpace.OPEN, this, adClose = {
            toHomeAc()
        })
    }
}