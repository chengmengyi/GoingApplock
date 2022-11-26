package com.demo.goingapplock.servers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.demo.goingapplock.GoingApp
import com.demo.goingapplock.R
import com.demo.goingapplock.log
import com.demo.goingapplock.manager.AppListManager
import com.demo.goingapplock.manager.getTopAppName
import com.demo.goingapplock.overlay.AppLockedOverlay
import kotlinx.coroutines.*

class AppLockedServers : Service() {
    private val mApp by lazy { GoingApp.mApp }
    private val NOTIFICATION_ID by lazy { mApp.packageName }

    private val keyBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = p1.getStringExtra("reason") ?: return
                if ("homekey".equals(reason, true) || "recentapps".equals(reason, true)) {
                    AppLockedOverlay.hideOverlay()
                }
            }
        }
    }

    private val builder by lazy {
        NotificationCompat.Builder(this, "AppLockServices")
            .setWhen(System.currentTimeMillis())
            .setContentTitle(getString(R.string.app_name))
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setChannelId(NOTIFICATION_ID)
    }

    private var showingApp = ""

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanel = NotificationChannel(NOTIFICATION_ID, "GoingApplock", IMPORTANCE_HIGH)
            chanel.enableLights(true)
            chanel.setShowBadge(true)
            manager.createNotificationChannel(chanel)
            builder.setContentTitle(getString(R.string.app_name))
            builder.setContentText("GoingApplock is running")
            builder.setSmallIcon(R.mipmap.ic_launcher_round)
            builder.setOngoing(true)
            builder.setOnlyAlertOnce(true)
            startForeground(1, builder.build())
        }
        AppLockedOverlay.createView(this)
        registerReceiver(
            keyBroadcastReceiver,
            IntentFilter(IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        check()
        return START_REDELIVER_INTENT
    }

    private var job: Job? = null
    private fun check() {
        if (job == null || job?.isCancelled == true) {
            job = GlobalScope.launch {
                while (true) {
                    delay(200)
                    val name = getTopAppName()
                    withContext(Dispatchers.Main) {
                        "name-->$name".log()
                        if (!name.isNullOrEmpty()) {
                            if (name != showingApp) {
                                if (AppListManager.locked(name) && Settings.canDrawOverlays(this@AppLockedServers)) {
                                    AppLockedOverlay.showOverlay()
                                } else {
                                    AppLockedOverlay.hideOverlay()
                                }
                            }
                            showingApp = name
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        job?.cancel()
        unregisterReceiver(keyBroadcastReceiver)
        super.onDestroy()
    }
}