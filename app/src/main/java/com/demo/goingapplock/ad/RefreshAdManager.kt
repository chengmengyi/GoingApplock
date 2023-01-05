package com.demo.goingapplock.ad

object RefreshAdManager {
    private val map= hashMapOf<String,Boolean>()

    fun canRefresh(key:String)=map[key]?:true

    fun setValue(key:String,value:Boolean){
        map[key]=value
    }

    fun resetMap(){
        map.keys.forEach {
            map[it]=true
        }
    }
}