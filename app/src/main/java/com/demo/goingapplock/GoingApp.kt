package com.demo.goingapplock

import android.app.Application
import com.tencent.mmkv.MMKV

class GoingApp:Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}