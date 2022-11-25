package com.demo.goingapplock.base

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar

abstract class BaseAc:AppCompatActivity() {
    protected lateinit var immersionBar: ImmersionBar
    var isResume=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        height()
        setContentView(layoutId())
        immersionBar= ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(true)
            init()
        }
        onView()
    }

    abstract fun layoutId():Int

    abstract fun onView()

    private fun height(){
        val metrics: DisplayMetrics = resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
    }

    override fun onResume() {
        super.onResume()
        isResume=true
    }

    override fun onPause() {
        super.onPause()
        isResume=false
    }
}