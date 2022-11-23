package com.demo.goingapplock.servers

import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.demo.goingapplock.manager.AppListManager
import com.demo.goingapplock.manager.getTopAppName
import com.demo.goingapplock.overlay.AppLockedOverlay
import kotlinx.coroutines.*

class AppLockedServers: Service()  {
    private var showingApp=""

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        AppLockedOverlay.createView(this)
        check()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

    private fun check(){
        GlobalScope.launch{
            while (true){
                delay(200)
                val name = getTopAppName()
                withContext(Dispatchers.Main){
                    if (!name.isNullOrEmpty()){
                        if (name!=showingApp){
                            if (AppListManager.locked(name)&& Settings.canDrawOverlays(this@AppLockedServers)){
                                AppLockedOverlay.showOverlay()
                            }else{
                                AppLockedOverlay.hideOverlay()
                            }
                        }
                        showingApp=name
                    }
                }
            }
        }
    }
}