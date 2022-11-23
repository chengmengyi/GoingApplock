package com.demo.goingapplock.ac.lock.fragment

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.goingapplock.R
import com.demo.goingapplock.adapter.AppListAdapter
import com.demo.goingapplock.bean.AppBean
import com.demo.goingapplock.interfaces.IAppLockInterface
import com.demo.goingapplock.manager.AppListManager
import com.demo.goingapplock.manager.show
import kotlinx.android.synthetic.main.fragment_locked_list.*

class LockedListFragment:Fragment() {
    private var iAppLockInterface:IAppLockInterface?=null
    private val appListAdapter by lazy { AppListAdapter(requireContext(),AppListManager.lockedList){ clickItem(it) } }

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
        return inflater.inflate(R.layout.fragment_locked_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTips()
        setAdapter()
    }

    private fun clickItem(appBean: AppBean){
        AppListManager.clickApp(appBean)
        appListAdapter.notifyDataSetChanged()
        iAppLockInterface?.unLockApp()
    }

    private fun setAdapter(){
        rv_locked.apply {
            layoutManager=LinearLayoutManager(requireContext())
            adapter=appListAdapter
        }
    }

    private fun setTips(){
        if(AppListManager.lockedList.isNotEmpty()){
            tv_tips.show(false)
            return
        }
        var str="Enter Unlocked Apps list, and click % of apps to lock it now!"
        val indexOf = str.indexOf("% ")
        val spannableString= SpannableString(str)
        val d = ContextCompat.getDrawable(requireContext(),R.drawable.unlock)!!
        d.setBounds(0, 0, d.minimumWidth, d.minimumHeight)
        spannableString.setSpan(
            ImageSpan(d),
            indexOf,
            indexOf+1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_tips.text=spannableString
        tv_tips.movementMethod = LinkMovementMethod.getInstance()
    }

    fun updateLockedList(){
        setTips()
        appListAdapter.notifyDataSetChanged()
    }
}