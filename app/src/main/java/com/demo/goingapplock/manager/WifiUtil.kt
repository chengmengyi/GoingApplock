package com.demo.goingapplock.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity

fun getWifiName(wifiManager: WifiManager):String {
    return try {
        val connectionInfo = wifiManager.connectionInfo
        var ssid = connectionInfo.ssid.toString()
        if (ssid.length>2&&ssid.startsWith('"')&&ssid.endsWith('"')){
            ssid=ssid.substring(1,ssid.length-1)
        }
        ssid
    }catch (e:Exception){
        ""
    }
}

fun getMaxSpeed(wifiManager: WifiManager):String{
    return try {
        val connectionInfo = wifiManager.connectionInfo.toString()
        val speed = connectionInfo.substring(
            connectionInfo.indexOf("Max Supported Rx Link speed: "),
            connectionInfo.indexOf(", Frequency")
        )
        speed.replace("Max Supported Rx Link speed: ","")
    }catch (e:Exception){
        ""
    }
}

fun getWifiMac(wifiManager: WifiManager):String{
    val wifiInfo: WifiInfo = wifiManager.connectionInfo
    return wifiInfo.bssid.toString()
}

fun Context.getWifiIp(wifiManager: WifiManager): String {
    val wifiMgr = applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
    return if (isWifiEnabled(wifiManager)) {
        val ipAsInt = wifiMgr.connectionInfo.ipAddress
        if (ipAsInt == 0) {
            ""
        } else {
            intToIp(ipAsInt)
        }
    } else {
        ""
    }
}

fun checkWifiHasPwd(wifiManager: WifiManager, ssid:String):Boolean{
    try {
        val connectionInfo = wifiManager.connectionInfo
        val scanResults = wifiManager.scanResults
        scanResults?.let {
            for (scanResult in it) {
                if (scanResult.SSID==ssid&&scanResult.BSSID==connectionInfo.bssid){
                    if(null!=scanResult.capabilities){
                        val capabilities = scanResult.capabilities.trim()
                        if(capabilities==""||capabilities=="[ESS]"){
                            return false
                        }
                    }
                }
            }
        }
    }catch (e:Exception){
        return true
    }
    return true
}

fun Context.isWifiEnabled(wifiManager: WifiManager): Boolean {
    return if (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED) {
        val connManager = getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        wifiInfo!!.isConnected
    } else {
        false
    }
}