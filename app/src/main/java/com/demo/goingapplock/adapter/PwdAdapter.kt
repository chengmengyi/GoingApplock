package com.demo.goingapplock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.goingapplock.R
import kotlinx.android.synthetic.main.item_pwd.view.*

class PwdAdapter(private val ctx:Context):RecyclerView.Adapter<PwdAdapter.MyView>() {
    private var pwdFail=false
    private var pwdLength=0

    fun setPwdLength(list: List<String>,pwdFail:Boolean){
        pwdLength=list.size
        this.pwdFail=pwdFail
        notifyDataSetChanged()
    }

    inner class MyView(view:View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PwdAdapter.MyView {
        return MyView(LayoutInflater.from(ctx).inflate(R.layout.item_pwd,parent,false))
    }

    override fun onBindViewHolder(holder: PwdAdapter.MyView, position: Int) {
        with(holder.itemView){
            if (pwdFail){
                iv_pwd.setImageResource(R.drawable.p3)
            }else{
                iv_pwd.setImageResource(if (pwdLength>=(position+1)) R.drawable.p2 else R.drawable.p1)
            }
        }
    }

    override fun getItemCount(): Int = 4

}