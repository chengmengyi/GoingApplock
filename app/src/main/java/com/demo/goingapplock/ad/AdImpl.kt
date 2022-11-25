package com.demo.goingapplock.ad

import android.app.Application
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions

class AdImpl(val app: Application) {
    fun loadAd(
        type: String,
        adId: String,
        loadFailed: () -> Unit,
        loadSuccess: (ad: Any) -> Unit,
        nativeAdClick: () -> Unit = {}
    ) {
        when (type) {
            "i" -> {
                loadI(adId, loadFailed, loadSuccess)
            }
            "o" -> {
                loadOpen(adId, loadFailed, loadSuccess)
            }
            "n" -> {
                loadN(adId, loadFailed, loadSuccess, nativeAdClick)
            }
        }
    }

    private fun loadN(
        adId: String,
        loadFailed: () -> Unit,
        loadSuccess: (ad: Any) -> Unit,
        nativeAdClick: () -> Unit = {}
    ) {
        val builder = AdLoader.Builder(app, adId)
        builder.forNativeAd { nativeAd ->
            loadSuccess.invoke(nativeAd)
        }
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                loadFailed.invoke()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                nativeAdClick.invoke()
            }
        }).withNativeAdOptions(
            NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
                .build()
        ).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun loadOpen(adId: String, loadFailed: () -> Unit, loadSuccess: (ad: Any) -> Unit) {
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            app,
            adId,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    loadFailed.invoke()
                }

                override fun onAdLoaded(p0: AppOpenAd) {
                    loadSuccess.invoke(p0)
                }
            }
        )
    }

    private fun loadI(adId: String, loadFailed: () -> Unit, loadSuccess: (ad: Any) -> Unit) {
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            app,
            adId,
            request,
            object : InterstitialAdLoadCallback() {

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    loadFailed.invoke()
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    loadSuccess.invoke(p0)
                }
            }
        )
    }

}