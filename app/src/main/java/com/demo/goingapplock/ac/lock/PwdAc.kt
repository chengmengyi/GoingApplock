package com.demo.goingapplock.ac.lock

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.goingapplock.GoingApp
import com.demo.goingapplock.R
import com.demo.goingapplock.ac.wifi.WifiScanAc
import com.demo.goingapplock.ad.AdSpace
import com.demo.goingapplock.ad.AdUtils
import com.demo.goingapplock.adapter.KeyAdapter
import com.demo.goingapplock.adapter.PwdAdapter
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.conf.GoingConf
import com.demo.goingapplock.dialog.NoticeDialog
import com.demo.goingapplock.dialog.SetPwdSuccessDialog
import com.demo.goingapplock.enums.PwdEnums
import com.demo.goingapplock.manager.PwdManager
import com.demo.goingapplock.manager.show
import kotlinx.android.synthetic.main.activity_pwd.*

class PwdAc:BaseAc() {
    private val pwd= arrayListOf<String>()
    private lateinit var enums:PwdEnums

    private val pwdAdapter by lazy { PwdAdapter(this) }

    private val keyAdapter by lazy { KeyAdapter(this){ clickKey(it) } }

    override fun layoutId(): Int = R.layout.activity_pwd

    override fun onView() {
        enums=intent.getSerializableExtra("enums") as PwdEnums
        immersionBar.statusBarView(view_top).init()
        iv_back.setOnClickListener { onBackPressed() }
        tv_reset_pwd.setOnClickListener { showRestPwdDialog() }
        setTitleImage()
        setAdapter()
        resetPwdTextShow()
    }

    private fun clickKey(key:String){
        when(key){
            "x"->{
                pwd.clear()
                setTitleImage()
                setErrorText("")
                pwdAdapter.setPwdLength(pwd,false)
            }
            "d"->{
                if(pwd.isNotEmpty()){
                    pwd.removeLast()
                    setTitleImage()
                    setErrorText("")
                    pwdAdapter.setPwdLength(pwd,false)
                }
            }
            else->{
                if(pwd.size<4){
                    pwd.add(key)
                    pwdAdapter.setPwdLength(pwd,false)
                    if(pwd.size>=4){
                        enterPwdFinish()
                    }
                }
            }
        }
    }

    private fun enterPwdFinish(){
        when(enums){
            PwdEnums.SET_PWD->{
                startActivity(Intent(this,PwdAc::class.java).apply {
                    putExtra("enums",PwdEnums.SET_PWD_AGAIN)
                    putExtra("pwd",getPwdStr())
                })
            }
            PwdEnums.CHECK_PWD->{
                if(PwdManager.checkPwd(getPwdStr())){
                    if (!AdUtils.show(AdSpace.PASS_ENTER,this, adClose = {
                            AdUtils.load(AdSpace.PASS_ENTER)
                            if (GoingApp.isAppResume)   toAppListAc()
                            finish()
                        })){
                        toAppListAc()
                    }
                }else{
                    errorLogic()
                    setTitleImage(error = true)
                    pwdAdapter.setPwdLength(pwd,true)
                }
            }
            PwdEnums.SET_PWD_AGAIN->{
                if(getPwdStr()==intent.getStringExtra("pwd")){
                    PwdManager.saveLocalPwd(getPwdStr())
                    SetPwdSuccessDialog{ toAppListAc() }.show(supportFragmentManager,"SetPwdSuccessDialog")
                }else{
                    errorLogic()
                    setTitleImage(error = true)
                    pwdAdapter.setPwdLength(pwd,true)
                }
            }
        }
    }

    private fun toAppListAc(){
        PwdManager.pwdErrorNum=3
        startActivity(Intent(this,AppListAc::class.java))
    }

    private fun getPwdStr():String{
        var p=""
        pwd.forEach {
            p+=it
        }
        return p
    }

    private fun setAdapter(){
        rv_pwd.apply {
            layoutManager=LinearLayoutManager(this@PwdAc,LinearLayoutManager.HORIZONTAL,false)
            adapter=pwdAdapter
        }
        rv_key.apply {
            layoutManager=GridLayoutManager(this@PwdAc,3)
            adapter=keyAdapter
        }
    }

    private fun setTitleImage(error:Boolean=false){
        when(enums){
            PwdEnums.SET_PWD->iv_title.setImageResource(R.drawable.set_pwd)
            PwdEnums.CHECK_PWD->iv_title.setImageResource(if(error) R.drawable.enter_pwd_fail else R.drawable.enter_pwd)
            PwdEnums.SET_PWD_AGAIN->iv_title.setImageResource(if(error)R.drawable.enter_pwd_again_fail else R.drawable.enter_pwd_again)
        }
    }

    private fun resetPwdTextShow(){
        tv_reset_pwd.show(enums==PwdEnums.CHECK_PWD)
    }

    private fun errorLogic(){
        when(enums){
            PwdEnums.CHECK_PWD->{
                PwdManager.addPwdErrorNum()
                setErrorText("You can also enter ${PwdManager.pwdErrorNum} times")
                if(PwdManager.pwdErrorNum<=0){
                    showRestPwdDialog()
                }
            }
            PwdEnums.SET_PWD_AGAIN->{
                setErrorText("The two passwords are inconsistent")
            }else->{

            }
        }
    }

    private fun showRestPwdDialog(){
        NoticeDialog(GoingConf.resetPwd){
            PwdManager.resetPwd()
            startActivity(Intent(this,PwdAc::class.java).apply {
                putExtra("enums",PwdEnums.SET_PWD)
            })
            finish()
        }.show(supportFragmentManager,"NoticeDialog")
    }

    private fun setErrorText(text:String){
        tv_pwd_error.text=text
    }

}