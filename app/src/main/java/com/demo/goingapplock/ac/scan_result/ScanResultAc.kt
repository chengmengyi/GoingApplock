package com.demo.goingapplock.ac.scan_result

import android.content.Intent
import android.provider.Settings
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.goingapplock.R
import com.demo.goingapplock.ac.lock.PwdAc
import com.demo.goingapplock.ac.vpn.VpnHomeAc
import com.demo.goingapplock.adapter.ScanResultAdapter
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.bean.ScanResultBean
import com.demo.goingapplock.conf.GoingConf
import com.demo.goingapplock.dialog.NoticeDialog
import com.demo.goingapplock.enums.PwdEnums
import com.demo.goingapplock.manager.AppListManager
import com.demo.goingapplock.manager.PwdManager
import com.demo.goingapplock.manager.hasLookAppPermission
import com.demo.goingapplock.manager.isNoOption
import com.demo.goingapplock.vpn.ConnectManager
import kotlinx.android.synthetic.main.activity_scan_result.*

class ScanResultAc:BaseAc() {
    private var wifiHasPwd=false
    private var openWifi=false
    private val list= arrayListOf<ScanResultBean>()

    override fun layoutId(): Int = R.layout.activity_scan_result

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        wifiHasPwd=intent.getBooleanExtra("wifiHasPwd",false)
        openWifi=intent.getBooleanExtra("openWifi",false)
        initList()
        setAdapter()
        setTopInfo()
        iv_back.setOnClickListener { finish() }
    }

    private fun setAdapter(){
        rv_list.apply {
            layoutManager=LinearLayoutManager(this@ScanResultAc)
            adapter=ScanResultAdapter(this@ScanResultAc,list){ clickItem(it) }
        }
    }

    private fun clickItem(index:Int){
        when(index){
            0->{
                if (!hasLookAppPermission() && isNoOption()) {
                    NoticeDialog(GoingConf.look) {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        startActivityForResult(intent, 100)
                    }.show(supportFragmentManager, "LookAppPermission")
                } else {
                    toPwdAc()
                }
            }
            1->{
                startActivity(Intent(this, VpnHomeAc::class.java))
            }
            2->{
                if (!openWifi){
                    startActivity(Intent(this, VpnHomeAc::class.java))
                }
            }
        }
    }

    private fun toPwdAc() {
        AppListManager.getAppList(this)
        startActivity(Intent(this, PwdAc::class.java).apply {
            putExtra("enums", if (PwdManager.hasSetPwd()) PwdEnums.CHECK_PWD else PwdEnums.SET_PWD)
        })
    }

    private fun initList(){
        list.add(
            ScanResultBean(
                R.drawable.scan_lock_selector,
                "App of the lock",
                if (AppListManager.lockedList.isEmpty()) "Let your sensitive applications (banking, dating, photos, etc.) lock it down and protect it from prying eyes" else "Let your sensitive applications (banking, dating, photos, etc.) lock it down and protect it from prying eyes",
                AppListManager.lockedList.isNotEmpty()
            )
        )
        list.add(
            ScanResultBean(
                R.drawable.scan_vpn_selector,
                "VPN",
                "Keep your browsing private using our secure VPN",
                ConnectManager.connected()
            )
        )

        list.add(
            ScanResultBean(
                R.drawable.scan_wifi_selector,
                "Wi-Fi security",
                if (wifiHasPwd) "Comodo alerts you to any suspicious activity on your network when using a public Wi-Fi network" else "Comodo alerts you to any suspicious activity on your network when using a public Wi-Fi network",
                wifiHasPwd
            )
        )
    }

    private fun setTopInfo(){
        var safe=true
        list.forEach {
            if (!it.safe){
                safe=false
            }
        }
        if(!safe){
            iv_top_bg.setImageResource(R.drawable.scan3)
            iv_top_icon.setImageResource(R.drawable.scan2)
            tv_title.text="You are in a risk environment"
        }else{
            tv_desc.text="${AppListManager.allAppSize()} application scans"
        }
    }
}