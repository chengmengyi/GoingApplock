package com.demo.goingapplock.ad

import androidx.annotation.Keep

@Keep
data class AdListBean(
    val gawsh:Int,
    val gawck:Int,
    val gaw_open: ArrayList<AdBean>,
    val gaw_home_nt: ArrayList<AdBean>,
    val gaw_wifien_it: ArrayList<AdBean>,
    val gaw_pass_it: ArrayList<AdBean>,
    val gaw_return_it: ArrayList<AdBean>,
    val gaw_vpn_home:ArrayList<AdBean>,
    val gaw_vpn_i:ArrayList<AdBean>,
    val gaw_vpn_result:ArrayList<AdBean>,
    val gaw_vpn_server:ArrayList<AdBean>,
)
