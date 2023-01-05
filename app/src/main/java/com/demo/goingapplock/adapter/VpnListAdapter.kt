package com.demo.goingapplock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.goingapplock.R
import com.demo.goingapplock.bean.VpnBean
import com.demo.goingapplock.getVpnLogo
import com.demo.goingapplock.vpn.ConnectManager
import com.demo.goingapplock.vpn.VpnInfoManager
import kotlinx.android.synthetic.main.item_vpn.view.*

class VpnListAdapter(
    private val context: Context,
    private val clickItem:(vpnbean:VpnBean)->Unit
):RecyclerView.Adapter<VpnListAdapter.VpnListView>() {
    private val list= arrayListOf<VpnBean>()
    init {
        list.add(VpnBean())
        list.addAll(VpnInfoManager.getAllVpnList())
    }

    inner class VpnListView(view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener {
                clickItem.invoke(list[layoutPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VpnListView {
        return VpnListView(LayoutInflater.from(context).inflate(R.layout.item_vpn,parent,false))
    }

    override fun onBindViewHolder(holder: VpnListView, position: Int) {
        with(holder.itemView){
            val vpnBean = list[position]
            tv_vpn_name.text=vpnBean.gawa_s_coun
            iv_vpn_logo.setImageResource(getVpnLogo(vpnBean.gawa_s_coun))

            val currentBean = ConnectManager.currentBean
            val b = vpnBean.gawa_s_coun == currentBean.gawa_s_coun && vpnBean.gawa_s_ip == currentBean.gawa_s_ip
            item_layout.isSelected=b
            iv_vpn_sel.isSelected=b
        }
    }

    override fun getItemCount(): Int = list.size
}