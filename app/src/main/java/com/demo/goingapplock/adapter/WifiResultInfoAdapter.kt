package com.demo.goingapplock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.goingapplock.R
import com.demo.goingapplock.bean.WifiInfoBean
import kotlinx.android.synthetic.main.item_wifi_result.view.*

class WifiResultInfoAdapter(
    private val context: Context,
    private val list:ArrayList<WifiInfoBean>
):RecyclerView.Adapter<WifiResultInfoAdapter.MyView>() {

    inner class MyView(view:View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(LayoutInflater.from(context).inflate(R.layout.item_wifi_result,parent,false))
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        with(holder.itemView){
            val wifiInfoBean = list[position]
            tv_key.text=wifiInfoBean.key
            tv_value.text=wifiInfoBean.value
        }
    }

    override fun getItemCount(): Int = list.size

}