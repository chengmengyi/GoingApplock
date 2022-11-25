package com.demo.goingapplock

import android.util.Log

fun String.log(tag: String="GoingApplock-Log") {
    if (!BuildConfig.DEBUG) return
    Log.i(tag, this)
}

fun String.loge(tag: String="GoingApplock-Log") {
    if (!BuildConfig.DEBUG) return
    Log.e(tag, this)
}
object Utils {

}