package com.demo.goingapplock

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.demo.goingapplock.GoingApp.Companion.isAppResume
import com.demo.goingapplock.ac.MainAc
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActivityLifeCycle : Application.ActivityLifecycleCallbacks {
    private var num = 0
    private var onBackgroundTime = -1L

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        "onActivityCreated $activity".log()
    }

    override fun onActivityStarted(activity: Activity) {
        "onActivityStarted $activity".log()
        if (onBackgroundTime != -1L && System.currentTimeMillis() - onBackgroundTime >= 3000L) {
            onBackgroundTime = -1L
            if ((activity !is MainAc)) {
                activity.startActivity(Intent(activity,MainAc::class.java))
            }
        }
        add()
    }

    override fun onActivityResumed(activity: Activity) {
        "onActivityResumed $activity".log()
        add()
    }

    override fun onActivityPaused(activity: Activity) {
        "onActivityPaused $activity".log()
        reduce()
    }

    override fun onActivityStopped(activity: Activity) {
        "onActivityStopped $activity".log()
        reduce()
        if (activity is AdActivity) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(2500L)
                if (onBackgroundTime != -1L && (!isAppResume) && !activity.isFinishing) {
                    activity.finish()
                }
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        "onActivityDestroyed $activity".log()
    }

    private fun add() {
        isAppResume = true
        onBackgroundTime = -1L
        num++
    }

    private fun reduce() {
        num--
        if (num <= 0) {
            isAppResume = false
            onBackgroundTime = System.currentTimeMillis()
        }
    }
}