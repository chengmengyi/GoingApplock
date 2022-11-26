package com.demo.goingapplock.manager

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.ArraySet
import com.demo.goingapplock.bean.AppBean
import com.demo.goingapplock.log
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object AppListManager {
    private val lockedAppNameList= ArraySet<String>()
    val lockedList= arrayListOf<AppBean>()
    val unLockedList= arrayListOf<AppBean>()

    fun getAppList(context:Context){
        getLockedAppNameList()
        val packageName = context.packageName
        GlobalScope.launch {
            val packageManager: PackageManager = context.packageManager
            val list = packageManager.getInstalledPackages(0)
            lockedList.clear()
            unLockedList.clear()
            for (packageInfo in list) {
                val packageN = packageInfo.applicationInfo.packageName
                val bean = AppBean(
                    icon = packageInfo.applicationInfo.loadIcon(packageManager),
                    name = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(),
                    packageName = packageN,
                    locked = lockedAppNameList.contains(packageN)
                )
                val flags = packageInfo.applicationInfo.flags
                if (flags and ApplicationInfo.FLAG_SYSTEM != 0) {

                } else {
                    if (bean.packageName!=packageName){
                        if (bean.locked){
                            lockedList.add(bean)
                        }else{
                            unLockedList.add(bean)
                        }
                    }
                }
            }
        }
    }

    fun clickApp(appBean: AppBean){
        if(appBean.locked){
            appBean.locked=!appBean.locked
            lockedAppNameList.remove(appBean.packageName)
            lockedList.remove(appBean)
            unLockedList.add(0,appBean)
        }else{
            appBean.locked=!appBean.locked
            lockedAppNameList.add(appBean.packageName)
            lockedList.add(0,appBean)
            unLockedList.remove(appBean)
        }
        saveLockedAppNameList()
    }

    private fun saveLockedAppNameList(){
        MMKV.defaultMMKV().encode("app",lockedAppNameList)
    }

    private fun getLockedAppNameList(){
        val app = MMKV.defaultMMKV().decodeStringSet("app")
        "getLockedAppNameList$app".log()
        if (!app.isNullOrEmpty()){
            lockedAppNameList.clear()
            lockedAppNameList.addAll(app)
        }
    }

    fun locked(name:String)=lockedAppNameList.contains(name)

    fun reset(){
        lockedAppNameList.clear()
        saveLockedAppNameList()
        unLockedList.addAll(lockedList)
        unLockedList.forEach {
            it.locked=false
        }
        lockedList.clear()
    }
}