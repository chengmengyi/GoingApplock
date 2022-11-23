package com.demo.goingapplock.bean

import android.graphics.drawable.Drawable

class AppBean(
    var name:String = "",
    var packageName:String = "",
    var icon : Drawable? = null,
    var locked:Boolean=false
) {
}