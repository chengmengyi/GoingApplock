package com.demo.goingapplock.manager

import com.tencent.mmkv.MMKV

object PwdManager {
    var pwdErrorNum=3

    fun addPwdErrorNum(){
        if(pwdErrorNum>0){
            pwdErrorNum--
        }
    }

    fun hasSetPwd()=readLocalPwd().isNotEmpty()

    private fun readLocalPwd()= MMKV.defaultMMKV().decodeString("pwd")?:""

    fun checkPwd(pwd:String)=pwd == readLocalPwd()

    fun saveLocalPwd(pwd:String){
        MMKV.defaultMMKV().encode("pwd",pwd)
    }

    fun resetPwd(){
        saveLocalPwd("")
        pwdErrorNum=3
        AppListManager.reset()
    }
}