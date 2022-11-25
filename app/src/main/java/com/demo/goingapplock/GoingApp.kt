package com.demo.goingapplock

import android.app.Application
import com.demo.goingapplock.ad.AdListBean
import com.demo.goingapplock.cache.GoingCache
import com.demo.goingapplock.cache.MmkvData
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

class GoingApp : Application() {
    companion object {
        var isAppResume = false
        lateinit var mApp: GoingApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        mApp = this
        MMKV.initialize(this)
        registerActivityLifecycleCallbacks(ActivityLifeCycle())
        MobileAds.initialize(this)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(
                    listOf(
                        "42DFDC23D4DB65F5F43CDC2A4DE65EE9",
                        "EBF26995014DA22B61C398591648F5ED"
                    )
                ).build()
        )
        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        Firebase.initialize(this)
        Firebase.remoteConfig
            .apply {
                setConfigSettingsAsync(
                    FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 10 else 3600)
                        .build()
                )
            }
            .fetchAndActivate()
            .addOnSuccessListener {
                val config = Firebase.remoteConfig
                val adKey = "gaw_ave"
                MmkvData.adConfig = config.getString(adKey).apply {
                    GoingCache.remoteAdListBean = Gson().fromJson(this, AdListBean::class.java)
                }
            }
    }
}