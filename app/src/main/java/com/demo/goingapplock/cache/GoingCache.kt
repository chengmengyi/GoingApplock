package com.demo.goingapplock.cache

import com.demo.goingapplock.ad.AdBean
import com.demo.goingapplock.ad.AdListBean
import com.demo.goingapplock.ad.AdSpace
import com.demo.goingapplock.ad.LOCAL_ADC_LIST_GOING_STR
import com.google.gson.Gson

object GoingCache {
    private var maxClickNum = 15
    private var maxShowNum = 50
    var clickNum = MmkvData.getCurDayNum("click")
        set(value) {
            field = value
            MmkvData.saveCurDayNum(field, "click")
        }
    var showNum = MmkvData.getCurDayNum("show")
        set(value) {
            field = value
            MmkvData.saveCurDayNum(field, "show")
        }

    var remoteAdListBean: AdListBean? = null
    private val localAdListBean by lazy {
        Gson().fromJson(
            LOCAL_ADC_LIST_GOING_STR,
            AdListBean::class.java
        )
    }

    private fun getAdConfigure(): AdListBean {
        return remoteAdListBean ?: localAdListBean
    }

    fun getAdConfigure(space: AdSpace): ArrayList<AdBean> {
        maxClickNum = getAdConfigure().gawck
        maxShowNum = getAdConfigure().gawsh
        val adList= arrayListOf<AdBean>()
        val list = when (space) {
            AdSpace.OPEN -> getAdConfigure().gaw_open
            AdSpace.HOME_NT -> getAdConfigure().gaw_home_nt
            AdSpace.WIFI_CLICK -> getAdConfigure().gaw_wifien_it
            AdSpace.PASS_ENTER -> getAdConfigure().gaw_pass_it
            AdSpace.RETURN_I -> getAdConfigure().gaw_return_it
            AdSpace.VPN_HOME-> getAdConfigure().gaw_vpn_home
            AdSpace.VPN_CONNECT-> getAdConfigure().gaw_vpn_i
            AdSpace.VPN_RESULT_BOTTOM-> getAdConfigure().gaw_vpn_result
            AdSpace.VPN_SERVER_BOTTOM-> getAdConfigure().gaw_vpn_server
        }
        if (!list.isNullOrEmpty()){
            list.sortWith { o1, o2 -> o2.gaw_wf - o1.gaw_wf }
            adList.addAll(list)
        }
        return adList
    }

    fun isAdLimit(): Boolean {
        return clickNum >= maxClickNum || showNum >= maxShowNum
    }

    fun refreshLimitData(){
        clickNum=MmkvData.getCurDayNum("click")
        showNum = MmkvData.getCurDayNum("show")
    }
}