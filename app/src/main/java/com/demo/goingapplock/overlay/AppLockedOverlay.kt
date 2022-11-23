package com.demo.goingapplock.overlay

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.goingapplock.R
import com.demo.goingapplock.adapter.KeyAdapter
import com.demo.goingapplock.adapter.PwdAdapter
import com.demo.goingapplock.manager.PwdManager

@SuppressLint("StaticFieldLeak")
object AppLockedOverlay {
    private var showing=false
    private var view: View?=null
    private val pwd= arrayListOf<String>()
    private var ivTitle:AppCompatImageView?=null
    private lateinit var windowManager: WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var pwdAdapter:PwdAdapter
    private lateinit var keyboardAdapter:KeyAdapter


    fun createView(context: Context){
        if(null==view){
            initView(context)
            setAdapter(context)
        }
    }

    private fun initView(context: Context){
        windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.gravity = Gravity.LEFT or Gravity.TOP
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                or WindowManager.LayoutParams.FLAG_FULLSCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT


        height(context)
        view = LayoutInflater.from(context).inflate(R.layout.overlay_pwd, null)
        ivTitle=view?.findViewById(R.id.iv_title)
        ivTitle?.setImageResource(R.drawable.enter_pwd)
    }

    private fun setAdapter(context: Context){
        val rvPwd = view?.findViewById<RecyclerView>(R.id.rv_pwd)
        val rvKeyboard = view?.findViewById<RecyclerView>(R.id.rv_key)
        pwdAdapter= PwdAdapter(context)
        keyboardAdapter= KeyAdapter(context){
            clickKey(it)
        }
        rvPwd?.apply {
            layoutManager= LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter= pwdAdapter
        }
        rvKeyboard?.apply {
            layoutManager= GridLayoutManager(context,3)
            adapter=keyboardAdapter
        }
    }

    private fun clickKey(key:String){
        when(key){
            "x"->{
                pwd.clear()
                ivTitle?.setImageResource(R.drawable.enter_pwd)
                pwdAdapter.setPwdLength(pwd,false)
            }
            "d"->{
                if(pwd.isNotEmpty()){
                    pwd.removeLast()
                    ivTitle?.setImageResource(R.drawable.enter_pwd)
                    pwdAdapter.setPwdLength(pwd,false)
                }
            }
            else->{
                if(pwd.size<4){
                    pwd.add(key)
                    pwdAdapter.setPwdLength(pwd,false)
                    if(pwd.size>=4){
                        checkPwd()
                    }
                }
            }
        }
    }

    private fun checkPwd(){
        val pwdStr = getPwdStr()
        if(PwdManager.checkPwd(pwdStr)){
            ivTitle?.setImageResource(R.drawable.enter_pwd)
            pwd.clear()
            pwdAdapter.setPwdLength(pwd,false)
            hideOverlay()
        }else{
            ivTitle?.setImageResource(R.drawable.enter_pwd_fail)
        }
    }

    fun showOverlay(){
        if (!showing){
            showing=true
            pwd.clear()
            ivTitle?.setImageResource(R.drawable.enter_pwd)
            pwdAdapter.setPwdLength(pwd,false)
            windowManager.addView(view, layoutParams)
        }
    }

    fun hideOverlay(){
        if (showing){
            showing=false
            windowManager.removeView(view)
        }
    }

    private fun getPwdStr():String{
        var p=""
        pwd.forEach {
            p+=it
        }
        return p
    }

    private fun height(context: Context) {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
    }
}