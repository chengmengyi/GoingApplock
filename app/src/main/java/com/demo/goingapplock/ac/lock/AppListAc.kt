package com.demo.goingapplock.ac.lock

import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.demo.goingapplock.R
import com.demo.goingapplock.ac.HomeAc
import com.demo.goingapplock.ac.lock.fragment.LockedListFragment
import com.demo.goingapplock.ac.lock.fragment.UnlockedListFragment
import com.demo.goingapplock.ad.AdSpace
import com.demo.goingapplock.ad.AdUtils
import com.demo.goingapplock.adapter.ViewPagerAdapter
import com.demo.goingapplock.base.BaseAc
import com.demo.goingapplock.interfaces.IAppLockInterface
import com.demo.goingapplock.servers.AppLockedServers
import kotlinx.android.synthetic.main.activity_app_list.*

class AppListAc:BaseAc(),IAppLockInterface{
    private var index=0
    private val list= arrayListOf<Fragment>()

    override fun layoutId(): Int = R.layout.activity_app_list

    override fun onView() {
        immersionBar.statusBarView(view_top).init()
        setIndex(0)
        setListener()
        setAdapter()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, AppLockedServers::class.java))
        }else{
            startService(Intent(this, AppLockedServers::class.java))
        }
    }

    private fun setAdapter(){
        list.add(LockedListFragment())
        list.add(UnlockedListFragment())
        viewpager.adapter=ViewPagerAdapter(list,supportFragmentManager)
        viewpager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    setIndex(position)
                }

                override fun onPageScrollStateChanged(state: Int) {

                }

            }
        )
    }
    
    private fun setListener(){
        view_locked.setOnClickListener {
            setIndex(0)
        }
        view_unlock.setOnClickListener {
            setIndex(1)
        }
        iv_back.setOnClickListener { onBackPressed() }
    }

    private fun setIndex(index:Int){
        this.index=index
        var left=index==0
        view_locked.isSelected=left
        tv_locked.isSelected=left
        view_unlock.isSelected=!left
        tv_unlock.isSelected=!left
        viewpager.currentItem=index
    }

    override fun lockApp() {
        try {
            val fragment = list[0]
            if (fragment is LockedListFragment){
                fragment.updateLockedList()
            }
        }catch (e:Exception){

        }
    }

    override fun unLockApp() {
        try {
            val fragment = list[1]
            if (fragment is UnlockedListFragment){
                fragment.updateUnlockedList()
            }
        }catch (e:Exception){

        }
    }

    private fun finishAc(){
        startActivity(Intent(this,HomeAc::class.java))
    }

    override fun onBackPressed() {
        if (!AdUtils.show(AdSpace.RETURN_I,this, adClose = {
                AdUtils.load(AdSpace.RETURN_I)
                finishAc()
            })){
            finishAc()
        }
    }
}