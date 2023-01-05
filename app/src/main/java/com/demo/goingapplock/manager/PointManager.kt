package com.demo.goingapplock.manager

import android.os.Bundle
import com.demo.goingapplock.log
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object PointManager {
    private val remoteConfig= Firebase.analytics

    fun setUser(plan:String){
        "==point==$plan".log("Going-Ad-Log")
        remoteConfig.setUserProperty("gawa_user",plan)
    }

    fun point(name:String){
        "==point==$name".log("Going-Ad-Log")
        remoteConfig.logEvent(name, Bundle())
    }
}