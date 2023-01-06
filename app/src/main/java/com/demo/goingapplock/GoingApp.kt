package com.demo.goingapplock

import android.app.ActivityManager
import android.app.Application
import android.content.Intent
import android.os.Build
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.demo.goingapplock.ac.HomeAc
import com.demo.goingapplock.ad.AdListBean
import com.demo.goingapplock.cache.GoingCache
import com.demo.goingapplock.cache.MmkvData
import com.demo.goingapplock.conf.GoingConf
import com.demo.goingapplock.manager.AppListManager
import com.demo.goingapplock.servers.AppLockedServers
import com.demo.goingapplock.vpn.VpnInfoManager
import com.github.shadowsocks.Core
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class GoingApp : Application() {
    companion object {
        var isAppResume = false
        var appFront=true
        var isIRUser=false
        var isAuthOverlayPermission=false
        lateinit var mApp: GoingApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        mApp = this
        Core.init(this,HomeAc::class)
        if (!packageName.equals(processName(this))){
            return
        }
        MMKV.initialize(this)
        registerActivityLifecycleCallbacks(ActivityLifeCycle())
        readReferrerStr()
        checkIRUser()
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
        AppListManager.getAppList(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, AppLockedServers::class.java))
        }else{
            startService(Intent(this, AppLockedServers::class.java))
        }
    }

    private fun initRemoteConfig() {
        VpnInfoManager.initLocalVpn()
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

                VpnInfoManager.parseConfigVpn(config.getString("gawa_servers"))
                VpnInfoManager.parseConfigCity(config.getString("smart_gawa"))

                val gawa_re = config.getString("gawa_re")
                if (gawa_re.isNotEmpty()){
                    GoingConf.gawa_re=gawa_re
                }

                val gawa_vpn_pop = config.getString("gawa_vpn_pop")
                if (gawa_vpn_pop.isNotEmpty()){
                    GoingConf.gawa_vpn_pop=gawa_vpn_pop
                }

                val gawa_ab = config.getString("gawa_ab")
                if (gawa_ab.isNotEmpty()){
                    GoingConf.gawa_ab=gawa_ab
                }
            }
    }

    private fun readReferrerStr(){
        if (MmkvData.readLocalReferrer().isEmpty()){
            val referrerClient = InstallReferrerClient.newBuilder(this).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    try {
                        referrerClient.endConnection()
                        when (responseCode) {
                            InstallReferrerClient.InstallReferrerResponse.OK -> {
                                val installReferrer = referrerClient.installReferrer.installReferrer
                                MmkvData.saveLocalReferrer(installReferrer)
                            }
                            else->{

                            }
                        }
                    } catch (e: Exception) {

                    }
                }
                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        }
    }

    private fun checkIRUser(){
        val country = Locale.getDefault().country
        if(country=="IR"){
            isIRUser=true
        }else{
            OkGo.get<String>("https://api.myip.com/")
                .execute(object : StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
//                        ipJson="""{"ip":"89.187.185.11","country":"United States","cc":"IR"}"""
                        try {
                            isIRUser= JSONObject(response?.body()?.toString()).optString("cc")=="IR"
                        }catch (e:Exception){

                        }
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                    }
                })
        }
    }

    private fun processName(applicationContext: Application): String {
        val pid = android.os.Process.myPid()
        var processName = ""
        val manager = applicationContext.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid === pid) {
                processName = process.processName
            }
        }
        return processName
    }
}