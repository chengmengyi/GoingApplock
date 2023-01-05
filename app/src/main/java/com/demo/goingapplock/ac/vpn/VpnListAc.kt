package com.demo.goingapplock.ac.vpn

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.goingapplock.R
import com.demo.goingapplock.ad.AdSpace
import com.demo.goingapplock.ad.AdUtils
import com.demo.goingapplock.ad.RefreshAdManager
import com.demo.goingapplock.adapter.VpnListAdapter
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.bean.VpnBean
import com.demo.goingapplock.dialog.NoticeDialog
import com.demo.goingapplock.vpn.ConnectManager
import com.demo.goingapplock.vpn.VpnInfoManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_vpn_list.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VpnListAc: BaseAc() {
    private val vpnAdapter by lazy { VpnListAdapter(this){ clickItem(it) } }

    override fun layoutId(): Int = R.layout.activity_vpn_list

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        AdUtils.load(AdSpace.VPN_CONNECT)
        rv_list.apply {
            layoutManager=LinearLayoutManager(this@VpnListAc)
            adapter=vpnAdapter
        }
        iv_back.setOnClickListener { onBackPressed() }
    }

    private fun clickItem(clickBean:VpnBean){
        val connected = ConnectManager.connected()
        val currentVpn = ConnectManager.currentBean
        if (connected&&currentVpn.gawa_s_ip!=clickBean.gawa_s_ip){
            showDisconnectDialog(clickBean)
        }else{
            if (connected){
                clickVpnFinish("",clickBean)
            }else{
                clickVpnFinish("connect",clickBean)
            }
        }
    }

    private fun showDisconnectDialog(clickBean: VpnBean){
        NoticeDialog("do you confirm to reconnect?"){
            clickVpnFinish("disconnect",clickBean)
        }
    }

    private fun clickVpnFinish(result:String,clickBean: VpnBean){
        ConnectManager.currentBean=clickBean
        setResult(15, Intent().apply {
            putExtra("result",result)
        })
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (RefreshAdManager.canRefresh(AdSpace.VPN_SERVER_BOTTOM.sName)){
            lifecycleScope.launch {
                delay(300)
                AdUtils.load(AdSpace.VPN_SERVER_BOTTOM)
                while (isResume) {
                    if (AdUtils.isHaveAd(AdSpace.VPN_SERVER_BOTTOM)){
                        val isShow= AdUtils.show(AdSpace.VPN_SERVER_BOTTOM,this@VpnListAc, nativeAdParent = ad_parent)
                        if (isShow){
                            AdUtils.load(AdSpace.VPN_SERVER_BOTTOM)
                            RefreshAdManager.setValue(AdSpace.VPN_SERVER_BOTTOM.sName,false)
                            break
                        }
                    }
                    delay(300)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (AdUtils.isHaveAd(AdSpace.VPN_CONNECT)){
            if (!AdUtils.show(AdSpace.VPN_CONNECT,this, adClose = {
                finish()
            })){
                finish()
            }
            return
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        RefreshAdManager.setValue(AdSpace.VPN_SERVER_BOTTOM.sName,true)
    }
}