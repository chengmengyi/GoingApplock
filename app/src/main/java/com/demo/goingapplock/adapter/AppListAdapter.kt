package com.demo.goingapplock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.goingapplock.R
import com.demo.goingapplock.bean.AppBean
import kotlinx.android.synthetic.main.item_app.view.*

class AppListAdapter(
    private val context:Context,
    private val list:ArrayList<AppBean>,
    private val item:(bean:AppBean)->Unit
):RecyclerView.Adapter<AppListAdapter.MyView>() {
    inner class MyView(view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener { item.invoke(list[layoutPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(LayoutInflater.from(context).inflate(R.layout.item_app,parent,false))
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        with(holder.itemView){
            val appBean = list[position]
            iv_app_logo.setImageDrawable(appBean.icon)
            tv_app_name.text=appBean.name
            iv_app_lock.setImageResource(if(appBean.locked) R.drawable.locked else R.drawable.unlock)

        }
    }

    override fun getItemCount(): Int = list.size
}