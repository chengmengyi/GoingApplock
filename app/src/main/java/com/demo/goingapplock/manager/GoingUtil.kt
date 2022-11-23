package com.demo.goingapplock.manager

import android.Manifest
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.lang.StringBuilder

fun Context.toast(string: String){
    Toast.makeText(this,string,Toast.LENGTH_LONG).show()
}

fun View.show(show:Boolean){
    visibility=if (show)View.VISIBLE else View.GONE
}

fun Context.hasLookAppPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        try {
            val packageManager = packageManager
            val info = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                info.uid,
                info.packageName
            )
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                info.uid,
                info.packageName
            ) == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    } else {
        true
    }
}

fun Context.isNoOption(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val packageManager = packageManager
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }
    return false
}

fun hasOverlayPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return true
    return Settings.canDrawOverlays(context)
}

fun Context.getTopAppName(): String {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        val activityManager=getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appTasks = activityManager.getRunningTasks(1)
        if (null != appTasks && !appTasks.isEmpty()) {
            return appTasks[0].topActivity!!.packageName
        }
    } else {
        //5.0以后需要用这方法
        val sUsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        var result = ""
        val event = UsageEvents.Event()
        val usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                result = event.packageName
            }
        }
        if (!TextUtils.isEmpty(result)) {
            return result
        }
    }
    return ""
}

fun intToIp(ipInt: Int): String {
    val sb = StringBuilder()
    sb.append(ipInt and 0xFF).append(".")
    sb.append(ipInt shr 8 and 0xFF).append(".")
    sb.append(ipInt shr 16 and 0xFF).append(".")
    sb.append(ipInt shr 24 and 0xFF)
    return sb.toString()
}