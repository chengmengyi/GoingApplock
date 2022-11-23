package com.demo.goingapplock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.goingapplock.R
import com.demo.goingapplock.bean.KeyBean
import kotlinx.android.synthetic.main.item_key.view.*

class KeyAdapter(
    private val context: Context,
    private val click:(key:String)->Unit
):RecyclerView.Adapter<KeyAdapter.MyView>() {
    private val list= arrayListOf<KeyBean>()
    init {
        list.add(KeyBean("1",R.drawable.k1))
        list.add(KeyBean("2",R.drawable.k2))
        list.add(KeyBean("3",R.drawable.k3))
        list.add(KeyBean("4",R.drawable.k4))
        list.add(KeyBean("5",R.drawable.k5))
        list.add(KeyBean("6",R.drawable.k6))
        list.add(KeyBean("7",R.drawable.k7))
        list.add(KeyBean("8",R.drawable.k8))
        list.add(KeyBean("9",R.drawable.k9))
        list.add(KeyBean("x",R.drawable.kc))
        list.add(KeyBean("0",R.drawable.k0))
        list.add(KeyBean("d",R.drawable.kd))
    }

    inner class MyView(view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener { click.invoke(list[layoutPosition].content)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(LayoutInflater.from(context).inflate(R.layout.item_key,parent,false))
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        with(holder.itemView){
            iv_key.setImageResource(list[position].res)
        }
    }

    override fun getItemCount(): Int = list.size
}