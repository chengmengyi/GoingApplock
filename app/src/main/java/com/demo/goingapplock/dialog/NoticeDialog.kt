package com.demo.goingapplock.dialog

import com.demo.goingapplock.R
import com.demo.goingapplock.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_notice.*

class NoticeDialog(
    private val content:String,
    private val sure:()->Unit,
):BaseDialog() {

    override fun layoutId(): Int = R.layout.dialog_notice

    override fun onView() {
        tv_content.text=content
        dialog?.setCancelable(false)
        tv_cancel.setOnClickListener {
            dismiss()
        }
        tv_sure.setOnClickListener {
            sure.invoke()
            dismiss()
        }
    }
}