package com.demo.goingapplock.vpn

import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.bean.VpnBean
import com.demo.goingapplock.interfaces.IConnectInterface
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ConnectManager:ShadowsocksConnection.Callback {
    private var baseAc:BaseAc?=null
    private var state = BaseService.State.Stopped
    var currentBean=VpnBean()
    var lastBean=VpnBean()
    private var iConnectInterface:IConnectInterface?=null
    private val sc=ShadowsocksConnection(true)

    fun onCreate(baseAc: BaseAc,iConnectInterface: IConnectInterface){
        this.baseAc=baseAc
        this.iConnectInterface=iConnectInterface
        sc.connect(baseAc,this)
    }

    fun connectVpn(){
        state=BaseService.State.Connecting
        GlobalScope.launch {
            if (currentBean.isFast()){
                DataStore.profileId= VpnInfoManager.getFastServer().getSourceId()
            }else{
                DataStore.profileId= currentBean.getSourceId()
            }
            Core.startService()
        }
        TimeManager.reset()
    }

    fun disconnectVpn(){
        state=BaseService.State.Stopping
        GlobalScope.launch {
            Core.stopService()
        }
    }

    fun connected()= state==BaseService.State.Connected

    fun disconnected()= state==BaseService.State.Stopped

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        this.state=state
        if (connected()){
            lastBean= currentBean
            TimeManager.startTime()
        }
        if (disconnected()){
            TimeManager.endTime()
            iConnectInterface?.disconnectSuccess()
        }
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        this.state=state
        if (connected()){
            TimeManager.startTime()
            lastBean= currentBean
            iConnectInterface?.connectSuccess()
        }
    }

    override fun onBinderDied() {
        baseAc?.let {
            sc.disconnect(it)
        }
    }

    fun onDestroy(){
        onBinderDied()
        baseAc=null
        iConnectInterface=null
    }
}