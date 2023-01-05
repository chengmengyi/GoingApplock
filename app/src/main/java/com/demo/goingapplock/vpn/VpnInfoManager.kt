package com.demo.goingapplock.vpn

import com.demo.goingapplock.ad.LOCAL_VPN_LIST_STR
import com.demo.goingapplock.bean.VpnBean
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

object VpnInfoManager {
    val localVpnList= arrayListOf<VpnBean>()
    val onlineVpnList= arrayListOf<VpnBean>()
    val onlineCityList= arrayListOf<String>()

    fun getAllVpnList()= onlineVpnList.ifEmpty { localVpnList }

    fun getFastServer():VpnBean{
        val allVpnList = getAllVpnList()
        if (!onlineCityList.isNullOrEmpty()){
            val filter = allVpnList.filter { onlineCityList.contains(it.gawa_s_city) }
            if (!filter.isNullOrEmpty()){
                return filter.random()
            }
        }
        return allVpnList.random()
    }

    fun initLocalVpn(){
        try {
            val jsonArray = JSONObject(LOCAL_VPN_LIST_STR).getJSONArray("gawa_all_s")
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                localVpnList.add(
                    VpnBean(
                        jsonObject.optString("gawa_s_account"),
                        jsonObject.optInt("gawa_s_port"),
                        jsonObject.optString("gawa_s_password"),
                        jsonObject.optString("gawa_s_coun"),
                        jsonObject.optString("gawa_s_city"),
                        jsonObject.optInt("gawa_s_num"),
                        jsonObject.optString("gawa_s_ip"),
                    )
                )
            }
            writeVpnToLocal(localVpnList)
        }catch (e: Exception){

        }
    }

    fun parseConfigVpn(vpn:String){
        try {
            val jsonArray = JSONObject(vpn).getJSONArray("gawa_all_s")
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                onlineVpnList.add(
                    VpnBean(
                        jsonObject.optString("gawa_s_account"),
                        jsonObject.optInt("gawa_s_port"),
                        jsonObject.optString("gawa_s_password"),
                        jsonObject.optString("gawa_s_coun"),
                        jsonObject.optString("gawa_s_city"),
                        jsonObject.optInt("gawa_s_num"),
                        jsonObject.optString("gawa_s_ip"),
                    )
                )
            }
            writeVpnToLocal(onlineVpnList)
        }catch (e: Exception){

        }

    }

    fun parseConfigCity(coun:String){
        try {
            val jsonArray = JSONArray(coun)
            for (index in 0 until jsonArray.length()){
                onlineCityList.add(jsonArray.optString(index))
            }
        }catch (e:Exception){

        }
    }

    private fun writeVpnToLocal(list: ArrayList<VpnBean>) {
        list.forEach { it.writeVpnInfo() }
    }
}