package com.demo.goingapplock.cache

import com.demo.goingapplock.conf.GoingConf
import com.demo.goingapplock.str2int
import com.tencent.mmkv.MMKV
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KProperty

private val mmkv: MMKV by lazy { MMKV.defaultMMKV() }

object MmkvData {
    var adConfig by StrCache()

    fun getCurDayNum(extraKey:String): Int {
        return mmkv.decodeInt("${SimpleDateFormat("yyyy-mm-dd").format(Date(System.currentTimeMillis()))}_$extraKey", 0)
    }

    fun saveCurDayNum(num: Int,extraKey:String) {
        mmkv.encode("${SimpleDateFormat("yyyy-mm-dd").format(Date(System.currentTimeMillis()))}_$extraKey", num)
    }

    fun readLocalReferrer()= MMKV.defaultMMKV().decodeString("referrer","")?:""

    fun saveLocalReferrer(installReferrer: String) {
        MMKV.defaultMMKV().encode("referrer",installReferrer)
    }

    fun isFirstLoad()=MMKV.defaultMMKV().decodeBool("first",true)

    fun setFirstLoad(){
        MMKV.defaultMMKV().encode("first",false)
    }

    fun randomPlanType() {
        if (GoingConf.planType.isEmpty()){
            if (GoingConf.gawa_ab.isEmpty()){
                val nextInt = Random().nextInt(100)
                GoingConf.planType=if (nextInt>20) "B" else "A"
            }else{
                val nextInt = Random().nextInt(100)
                GoingConf.planType=if (str2int(GoingConf.gawa_ab)>=nextInt) "B" else "A"
            }
        }
    }

    class IntCache(private val extraKey: String = "") {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
            return mmkv.decodeInt("${property.name}_$extraKey", 0)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            mmkv.encode("${property.name}_$extraKey", value)
        }
    }

    class StrCache(private val extraKey: String = "") {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return mmkv.decodeString("${property.name}_$extraKey") ?: ""
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            mmkv.encode("${property.name}_$extraKey", value)
        }
    }
}