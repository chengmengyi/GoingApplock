package com.demo.goingapplock.ad

import com.demo.goingapplock.R

enum class AdSpace(
    val sName: String,
    var isLoading: Boolean,
    val container: AdContainer,
    val nativeAdId: Int
) {
    OPEN("gaw_open", false, AdContainer(), -1),
    HOME_NT("gaw_home_nt", false, AdContainer(), R.layout.ad_home_layout_container),
    WIFI_CLICK("gaw_wifien_it", false, AdContainer(), -1),
    PASS_ENTER("gaw_pass_it", false, AdContainer(), -1),
    RETURN_I("gaw_return_it", false, AdContainer(), -1),
    VPN_HOME("gaw_home_nt", false, AdContainer(), R.layout.ad_home_layout_container),
    VPN_RESULT_BOTTOM("gaw_vpn_result", false, AdContainer(), R.layout.ad_result_layout_container),
    VPN_SERVER_BOTTOM("gaw_vpn_server", false, AdContainer(), R.layout.ad_home_layout_container),
    VPN_CONNECT("gaw_vpn_i", false, AdContainer(), -1),
}