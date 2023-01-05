package com.demo.goingapplock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.goingapplock.R
import com.demo.goingapplock.bean.ScanResultBean
import com.demo.goingapplock.manager.show
import kotlinx.android.synthetic.main.item_scan_result.view.*

class ScanResultAdapter(
    private val context: Context,
    private val list:ArrayList<ScanResultBean>,
    private val clickItem:(index:Int)->Unit
):RecyclerView.Adapter<ScanResultAdapter.ScanResultView>() {
    inner class ScanResultView(view:View):RecyclerView.ViewHolder(view){
        private val tvGo=view.findViewById<AppCompatTextView>(R.id.tv_go)
        init {
            tvGo.setOnClickListener {
                clickItem.invoke(layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanResultView {
        return ScanResultView(LayoutInflater.from(context).inflate(R.layout.item_scan_result,parent,false))
    }

    override fun onBindViewHolder(holder: ScanResultView, position: Int) {
        with(holder.itemView){
            val scanResultBean = list[position]
            tv_go.show(!scanResultBean.safe)
            tv_lock_desc.text=scanResultBean.desc
            tv_lock_title.text=scanResultBean.title
            iv_lock_icon.isSelected=scanResultBean.safe
            iv_lock_icon.setImageResource(scanResultBean.icon)
        }
    }

    override fun getItemCount(): Int = list.size
}