package com.demo.goingapplock.ad

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.demo.goingapplock.GoingApp
import com.demo.goingapplock.R
import com.demo.goingapplock.cache.GoingCache
import com.demo.goingapplock.log
import com.demo.goingapplock.loge
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

object AdUtils {
    private val adImpl by lazy { AdImpl(GoingApp.mApp) }
    fun load(space: AdSpace, adLimit: () -> Unit = {}, retryNum: Int = 0) {
        if (space.isLoading) {
            return
        }
        if (space.container.ad != null) {
            logI("have ad cache==  ${space.sName}")
            return
        }
        if (GoingCache.isAdLimit()) {
            logE("ad limit --->")
            adLimit.invoke()
            return
        }
        val iterator = GoingCache.getAdConfigure(space).iterator()
        if (iterator.hasNext()) {
            space.isLoading = true
            load(space, iterator, retryNum)
        } else {
            logE("no find ad configure ${space.sName}")
        }
    }

    private fun load(space: AdSpace, iterator: Iterator<AdBean>, retryNum: Int) {
        val bean = iterator.next()
        logI("load ad ${space.sName} --${bean.gaw_wf}---${bean.gaw_id}")
        adImpl.loadAd(bean.gaw_tp, bean.gaw_id, {
            logE("load ad failed ${space.sName} --${bean.gaw_wf}---${bean.gaw_id}")
            if (iterator.hasNext()) {
                load(space, iterator, retryNum)
            } else {
                space.isLoading = false
                if (retryNum > 0) {
                    load(space, retryNum = retryNum - 1)
                }
            }
        }, {
            logI("load ad success ${space.sName} --${bean.gaw_wf}---${bean.gaw_id}")
            space.isLoading = false
            space.container.ad = it
        }, nativeAdClick = {
            addClickAdNum(space)
        })
    }

    fun isHaveAd(space: AdSpace): Boolean {
        return space.container.ad != null
    }

    private fun addClickAdNum(space: AdSpace) {
        GoingCache.clickNum++
    }

    private fun addShowAdNum(space: AdSpace) {
        GoingCache.showNum++
        logI("show ad--> ${space.sName}")
    }

    fun show(
        space: AdSpace,
        activity: AppCompatActivity,
        adClose: () -> Unit={},
        nativeAdParent: ViewGroup? = null
    ): Boolean {
        if (activity.lifecycle.currentState!=Lifecycle.State.RESUMED) return false
        val callback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                adClose.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                adClose.invoke()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                addClickAdNum(space)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                addShowAdNum(space)
            }
        }
        when (val ad = space.container.ad) {
            is AppOpenAd -> {
                ad.fullScreenContentCallback = callback
                ad.show(activity)
            }
            is InterstitialAd -> {
                ad.fullScreenContentCallback = callback
                ad.show(activity)
            }
            is NativeAd -> {
                if (space.nativeAdId == -1 || nativeAdParent == null) {
                    logE("$space ---not configure native ")
                    return false
                }
                showNativeView(ad, nativeAdParent, activity, space.nativeAdId)
            }
            else -> return false
        }
        space.container.ad = null
        return true
    }

    private fun showNativeView(
        nativeAd: NativeAd,
        adParent: ViewGroup,
        activity: Activity,
        layoutId: Int
    ) {
        val adView = activity.layoutInflater.inflate(layoutId, null) as NativeAdView
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById(R.id.tv_app_name)
        adView.callToActionView = adView.findViewById(R.id.installBtn)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.bodyView = adView.findViewById(R.id.tv_des)
        (adView.headlineView as TextView).text = nativeAd.headline
        nativeAd.mediaContent?.let {
            adView.mediaView?.setMediaContent(it)
        }
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.INVISIBLE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
        adParent.removeAllViews()
        adParent.addView(adView)
    }

    private fun logI(msg: String) {
        msg.log("Going-Ad-Log")
    }

    private fun logE(msg: String) {
        msg.loge("Going-Ad-Log")
    }

    fun removeAllAdCache(){
        removeAdCache(AdSpace.OPEN)
        removeAdCache(AdSpace.HOME_NT)
        removeAdCache(AdSpace.WIFI_CLICK)
        removeAdCache(AdSpace.RETURN_I)
        removeAdCache(AdSpace.PASS_ENTER)
        removeAdCache(AdSpace.VPN_HOME)
        removeAdCache(AdSpace.VPN_CONNECT)
        removeAdCache(AdSpace.VPN_RESULT_BOTTOM)
        removeAdCache(AdSpace.VPN_SERVER_BOTTOM)


        load(AdSpace.OPEN, retryNum = 1)
        load(AdSpace.HOME_NT)
        load(AdSpace.WIFI_CLICK)
        load(AdSpace.RETURN_I)
        load(AdSpace.PASS_ENTER)
        load(AdSpace.VPN_HOME)
        load(AdSpace.VPN_CONNECT)
        load(AdSpace.VPN_RESULT_BOTTOM)
        load(AdSpace.VPN_SERVER_BOTTOM)
    }

    private fun removeAdCache(space: AdSpace) {
        space.container.ad=null
        space.isLoading=false
    }
}