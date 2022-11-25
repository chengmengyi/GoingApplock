package com.demo.goingapplock.ad

class AdContainer {
    private var saveAdTime = 0L
    var ad: Any? = null
        set(value) {
            if (value != ad) {
                saveAdTime = System.currentTimeMillis()
            }
            field = value
        }
        get() {
            if (System.currentTimeMillis() - saveAdTime > 60 * 1000 * 50) return null
            return field
        }
}