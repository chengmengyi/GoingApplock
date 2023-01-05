package com.demo.goingapplock.dialog

import com.demo.goingapplock.R
import com.demo.goingapplock.base.BaseDialog
import com.demo.goingapplock.manager.PointManager
import kotlinx.android.synthetic.main.dialog_vpn.*

class VpnDialog(private val callback:(sure:Boolean)->Unit):BaseDialog() {
    override fun layoutId(): Int = R.layout.dialog_vpn

    override fun onView() {
        dialog?.setCancelable(false)
        PointManager.point("gawa_pop_sh")
        tv_sure.setOnClickListener {
            PointManager.point("gawa_pop_click")
            dismiss()
            callback.invoke(true)
        }
        iv_cancel.setOnClickListener {
            PointManager.point("gawa_pop_cancel")
            dismiss()
            callback.invoke(false)
        }
    }
}