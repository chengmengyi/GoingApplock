package com.demo.goingapplock

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.tencent.mmkv.MMKV

fun String.log(tag: String="GoingApplock-Log") {
    if (!BuildConfig.DEBUG) return
    Log.i(tag, this)
}

fun String.loge(tag: String="GoingApplock-Log") {
    if (!BuildConfig.DEBUG) return
    Log.e(tag, this)
}

fun getVpnLogo(coun:String)=when(coun){
    "United States"->R.drawable.unitedstates
    "Australia"->R.drawable.australia
    "Brazil"->R.drawable.brazil
    "Netherlands"->R.drawable.netherlands
    "Canada"->R.drawable.canada
    "Belgium"->R.drawable.belgium
    "South Korea"->R.drawable.koreasouth
    else->R.drawable.fast
}

fun String.isBuyUser()=contains("fb4a")||
        contains("gclid")||
        contains("not%20set")||
        contains("youtubeads")||
        contains("%7B%22")

fun Context.netStatus():Int{
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    if (null!=activeNetworkInfo&&activeNetworkInfo.isConnected){
        if (activeNetworkInfo.type==ConnectivityManager.TYPE_WIFI){
            return 2
        }else if (activeNetworkInfo.type==ConnectivityManager.TYPE_MOBILE){
            return 0
        }
    }else{
        return 1
    }
    return 1
}

object Utils {

}