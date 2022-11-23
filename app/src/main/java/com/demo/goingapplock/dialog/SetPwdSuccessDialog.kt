package com.demo.goingapplock.dialog

import com.demo.goingapplock.R
import com.demo.goingapplock.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_notice.*

class SetPwdSuccessDialog(private val sure:()->Unit,):BaseDialog() {

    override fun layoutId(): Int = R.layout.dialog_set_pwd_success

    override fun onView() {
        dialog?.setCancelable(false)
        tv_sure.setOnClickListener {
            sure.invoke()
            dismiss()
        }
    }
}