package com.demo.goingapplock.ac

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.demo.goingapplock.GoingApp
import com.demo.goingapplock.R
import com.demo.goingapplock.ac.lock.PwdAc
import com.demo.goingapplock.ac.vpn.VpnHomeAc
import com.demo.goingapplock.ac.wifi.WifiScanAc
import com.demo.goingapplock.ad.AdSpace
import com.demo.goingapplock.ad.AdUtils
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.conf.GoingConf
import com.demo.goingapplock.dialog.NoticeDialog
import com.demo.goingapplock.enums.PwdEnums
import com.demo.goingapplock.manager.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_content.*
import kotlinx.android.synthetic.main.activity_home_drawer.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeAc : BaseAc() {
    private var scanning = false
    private var wifiHasPwd = false
    private var rotateAnimation: ObjectAnimator? = null

    override fun layoutId(): Int = R.layout.activity_home

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        setListener()
//        tv_scan_result.isSelected=true
//        updateScanningUI()
    }

    private fun setListener() {
        llc_app_lock.setOnClickListener {
            if (drawerIsOpen()) {
                return@setOnClickListener
            }
            if (!hasLookAppPermission() && isNoOption()) {
                NoticeDialog(GoingConf.look) {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    startActivityForResult(intent, 100)
                }.show(supportFragmentManager, "LookAppPermission")
            } else {
                toPwdAc()
            }
        }
        llc_wifi.setOnClickListener {
            if (drawerIsOpen()) {
                return@setOnClickListener
            }
            showIAd()
        }
        llc_vpn.setOnClickListener {
            if (drawerIsOpen()) {
                return@setOnClickListener
            }
            startActivity(Intent(this,VpnHomeAc::class.java))
        }
//        iv_scan.setOnClickListener {
//            if(drawerIsOpen()){
//                return@setOnClickListener
//            }
//            startScan()
//        }
        iv_set.setOnClickListener {
            if (!drawerIsOpen()) {
                drawer_layout.open()
            }
        }

        llc_privacy.setOnClickListener { startActivity(Intent(this, WebAc::class.java)) }

        llc_contact.setOnClickListener {
            try {
                val uri = Uri.parse("mailto:${GoingConf.EMAIL}")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                startActivity(intent)
            } catch (e: Exception) {
                toast("Contact us by email：${GoingConf.EMAIL}")
            }
        }
        llc_share.setOnClickListener {
            val pm = packageManager
            val packageName =
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=${packageName}"
            )
            startActivity(Intent.createChooser(intent, "share"))
        }
        llc_update.setOnClickListener {
            val packName = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_ACTIVITIES
            ).packageName
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=$packName")
            }
            startActivity(intent)
        }
    }

    private fun drawerIsOpen() = drawer_layout.isOpen

//    private fun startScan(){
//        if(scanning){
//            return
//        }
//        scanning=true
//        iv_scan.setImageResource(R.drawable.home7)
//        updateScanningUI()
//        startRotation()
//        startScanProgress()
//    }
//
//    private fun startScanProgress(){
//        ValueAnimator.ofInt(0, 100).apply {
//            duration=3000L
//            interpolator = LinearInterpolator()
//            addUpdateListener {
//                val progress = it.animatedValue as Int
//                tv_scan_progress.text="$progress%"
//                val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
//                wifiHasPwd = if(isWifiEnabled(wifiManager)){
//                    checkWifiHasPwd(wifiManager, getWifiName(wifiManager))
//                }else{
//                    true
//                }
//            }
//            doOnEnd {
//                updateScanFinishUI()
//            }
//            start()
//        }
//    }
//
//    private fun startRotation(){
//        rotateAnimation= ObjectAnimator.ofFloat(iv_scan, "rotation", 0f, 360f).apply {
//            duration=500L
//            repeatCount= ValueAnimator.INFINITE
//            repeatMode= ObjectAnimator.RESTART
//            start()
//        }
//    }
//
//    private fun stopRotation(){
//        rotateAnimation?.cancel()
//        rotateAnimation=null
//        iv_scan.rotation=0f
//    }
//
//    private fun updateScanningUI(){
//        iv_start_scan.show(!scanning)
//        tv_scan_progress.show(scanning)
//    }
//
//    private fun updateScanFinishUI(){
//        stopRotation()
//        scanning=false
//        iv_start_scan.show(false)
//        tv_scan_progress.show(false)
//        tv_scan_result.text=if (wifiHasPwd) "Safe" else "Unsafe"
//        tv_scan_result.isSelected=wifiHasPwd
//        iv_scan.setImageResource(R.drawable.home8)
//    }

    private fun toPwdAc() {
        AppListManager.getAppList(this)
        startActivity(Intent(this, PwdAc::class.java).apply {
            putExtra("enums", if (PwdManager.hasSetPwd()) PwdEnums.CHECK_PWD else PwdEnums.SET_PWD)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (hasLookAppPermission()) {
                toPwdAc()
            } else {
                toast("No permission")
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) return true
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            101
        )
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, WifiScanAc::class.java))
            }
        }
    }

    private var isRefreshNativeAd = true
    override fun onResume() {
        super.onResume()
        showNativeAd()
    }

    override fun onStop() {
        super.onStop()
        if (!GoingApp.isAppResume){
            isRefreshNativeAd = true
        }
    }

    private fun showNativeAd() {
        if (isRefreshNativeAd) {
            lifecycleScope.launch {
                delay(300)
                AdUtils.load(AdSpace.HOME_NT)
                while (isResume) {
                    if (AdUtils.isHaveAd(AdSpace.HOME_NT)){
                       val isShow= AdUtils.show(AdSpace.HOME_NT,this@HomeAc, nativeAdParent = ad_parent)
                        if (isShow){
                            AdUtils.load(AdSpace.HOME_NT)
                            isRefreshNativeAd = false
                            break
                        }
                    }
                    delay(300)
                }
            }
        }
    }

    private fun showIAd(){
        AdUtils.load(AdSpace.WIFI_CLICK)
        if (checkLocationPermission()) {
            if (!AdUtils.show(AdSpace.WIFI_CLICK,this, adClose = {
                    AdUtils.load(AdSpace.WIFI_CLICK)
                    if (GoingApp.isAppResume)   startActivity(Intent(this, WifiScanAc::class.java))
                })){
                startActivity(Intent(this, WifiScanAc::class.java))
            }
        }
    }
}