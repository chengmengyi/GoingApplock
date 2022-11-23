package com.demo.goingapplock.ac.wifi

import android.net.wifi.WifiManager
import com.demo.goingapplock.R
import com.demo.goingapplock.base.BaseAc
import kotlinx.android.synthetic.main.activity_wifi_scan.*
import android.content.Intent
import com.demo.goingapplock.bean.WifiInfoBean
import com.demo.goingapplock.manager.*
import kotlinx.coroutines.*


class WifiScanAc:BaseAc() {
    private lateinit var job:Job

    override fun layoutId(): Int = R.layout.activity_wifi_scan

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        iv_back.setOnClickListener { finish() }
        getWifiInfo()
    }

    private fun getWifiInfo(){
        job=GlobalScope.launch {
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            if (isWifiEnabled(wifiManager)){
                val wifiName = getWifiName(wifiManager)
                val wifiIp = getWifiIp(wifiManager)
                val maxSpeed = getMaxSpeed(wifiManager)
                val macAddress = getWifiMac(wifiManager)
                delay(3000)
                withContext(Dispatchers.Main){
                    toResultAc(wifiName, wifiIp, maxSpeed, macAddress,checkWifiHasPwd(wifiManager,wifiName))
                }
            }else{
                delay(3000)
                withContext(Dispatchers.Main){
                    toResultAc("","","","",true)
                }
            }
        }
    }

    private fun toResultAc(wifiName:String,wifiIp:String,maxSpeed:String,macAddress:String,hasPwd:Boolean){
        val list= arrayListOf<WifiInfoBean>()
        list.add(WifiInfoBean("WiFi Name：",wifiName))
        list.add(WifiInfoBean("Maximum link speed：",maxSpeed))
        list.add(WifiInfoBean("Assigned IP Address：",wifiIp))
        list.add(WifiInfoBean("Wi-Fi MAC Address：",macAddress))
        startActivity(Intent(this,WifiScanResultAc::class.java).apply {
            putExtra("list",list)
            putExtra("hasPwd",hasPwd)
        })
        finish()
    }

//    private fun getWifiName(wifiManager: WifiManager):String {
//        return try {
//            val connectionInfo = wifiManager.connectionInfo
//            var ssid = connectionInfo.ssid.toString()
//            if (ssid.length>2&&ssid.startsWith('"')&&ssid.endsWith('"')){
//                ssid=ssid.substring(1,ssid.length-1)
//            }
//            ssid
//        }catch (e:Exception){
//            ""
//        }
//    }

//    private fun getMaxSpeed(wifiManager: WifiManager):String{
//        return try {
//            val connectionInfo = wifiManager.connectionInfo.toString()
//            val speed = connectionInfo.substring(
//                connectionInfo.indexOf("Max Supported Rx Link speed: "),
//                connectionInfo.indexOf(", Frequency")
//            )
//            speed.replace("Max Supported Rx Link speed: ","")
//        }catch (e:Exception){
//            ""
//        }
//    }
//
//    private fun getWifiMac(wifiManager: WifiManager):String{
//        val wifiInfo: WifiInfo = wifiManager.connectionInfo
//        return wifiInfo.bssid.toString()
//    }
//
//    private fun getWifiIp(wifiManager: WifiManager): String {
//        val wifiMgr = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
//        return if (isWifiEnabled(wifiManager)) {
//            val ipAsInt = wifiMgr.connectionInfo.ipAddress
//            if (ipAsInt == 0) {
//                ""
//            } else {
//                intToIp(ipAsInt)
//            }
//        } else {
//            ""
//        }
//    }
//
//    private fun checkWifiHasPwd(wifiManager: WifiManager,ssid:String):Boolean{
//        try {
//            val connectionInfo = wifiManager.connectionInfo
//            val scanResults = wifiManager.scanResults
//            scanResults?.let {
//                for (scanResult in it) {
//                    if (scanResult.SSID==ssid&&scanResult.BSSID==connectionInfo.bssid){
//                        if(null!=scanResult.capabilities){
//                            val capabilities = scanResult.capabilities.trim()
//                            if(capabilities==""||capabilities=="[ESS]"){
//                                return false
//                            }
//                        }
//                    }
//                }
//            }
//        }catch (e:Exception){
//            return true
//        }
//        return true
//    }
//
//    private fun isWifiEnabled(wifiManager: WifiManager): Boolean {
//        return if (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED) {
//            val connManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//            val wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//            wifiInfo!!.isConnected
//        } else {
//            false
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel();
    }
}