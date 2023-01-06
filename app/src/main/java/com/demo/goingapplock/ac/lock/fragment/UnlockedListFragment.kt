package com.demo.goingapplock.ac.lock.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.goingapplock.GoingApp.Companion.isAuthOverlayPermission
import com.demo.goingapplock.R
import com.demo.goingapplock.adapter.AppListAdapter
import com.demo.goingapplock.bean.AppBean
import com.demo.goingapplock.conf.GoingConf
import com.demo.goingapplock.dialog.NoticeDialog
import com.demo.goingapplock.interfaces.IAppLockInterface
import com.demo.goingapplock.manager.AppListManager
import com.demo.goingapplock.manager.hasOverlayPermission
import kotlinx.android.synthetic.main.fragment_unlocked_list.*

class UnlockedListFragment:Fragment() {
    private var iAppLockInterface:IAppLockInterface?=null
    private val appListAdapter by lazy { AppListAdapter(requireContext(),AppListManager.unLockedList){ clickItem(it) } }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            iAppLockInterface=context as IAppLockInterface
        }catch (e:Exception){

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_unlocked_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
    }

    private fun clickItem(appBean: AppBean){
        if(!hasOverlayPermission(requireContext())){
            NoticeDialog(GoingConf.overlay){
                isAuthOverlayPermission=true
                val intent= Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${requireContext().packageName}"))
                startActivityForResult(intent, 101)
            }.show(childFragmentManager,"OverlayPermission")
            return
        }
        AppListManager.clickApp(appBean)
        appListAdapter.notifyDataSetChanged()
        iAppLockInterface?.lockApp()
    }

    private fun setAdapter(){
        rv_unlock.apply {
            layoutManager= LinearLayoutManager(requireContext())
            adapter=appListAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        isAuthOverlayPermission=false
    }

    fun updateUnlockedList(){
        appListAdapter.notifyDataSetChanged()
    }
}