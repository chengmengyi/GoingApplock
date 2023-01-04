package com.demo.goingapplock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.goingapplock.R

class VpnListAdapter(
    private val context: Context,
):RecyclerView.Adapter<VpnListAdapter.VpnListView>() {

    inner class VpnListView(view:View):RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VpnListView {
        return VpnListView(LayoutInflater.from(context).inflate(R.layout.item_vpn,parent,false))
    }

    override fun onBindViewHolder(holder: VpnListView, position: Int) {

    }

    override fun getItemCount(): Int = 10
}