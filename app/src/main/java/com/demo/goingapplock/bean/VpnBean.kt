package com.demo.goingapplock.bean

import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager

class VpnBean(
    var gawa_s_account:String="",
    var gawa_s_port:Int=0,
    var gawa_s_password:String="",
    var gawa_s_coun:String="Super Fast Servers",
    var gawa_s_city:String="",
    var gawa_s_num:Int=0,
    var gawa_s_ip:String=""
) {

    fun isFast()=gawa_s_coun=="Super Fast Servers"&&gawa_s_ip.isEmpty()

    fun getSourceId():Long{
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.host==gawa_s_ip&&it.remotePort==gawa_s_port){
                return it.id
            }
        }
        return 0L
    }

    fun writeVpnInfo(){
        val profile = Profile(
            id = 0L,
            name = "${gawa_s_coun} - ${gawa_s_city}",
            host = gawa_s_ip,
            remotePort = gawa_s_port,
            password = gawa_s_password,
            method = gawa_s_account
        )
        var id:Long?=null
        ProfileManager.getActiveProfiles()?.forEach {
            if(it.remotePort==profile.remotePort&&it.host==profile.host){
                id=it.id
                return@forEach
            }
        }
        if (null==id){
            ProfileManager.createProfile(profile)
        }else{
            profile.id=id!!
            ProfileManager.updateProfile(profile)
        }
    }
}