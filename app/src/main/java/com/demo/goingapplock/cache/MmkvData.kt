package com.demo.goingapplock.cache

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