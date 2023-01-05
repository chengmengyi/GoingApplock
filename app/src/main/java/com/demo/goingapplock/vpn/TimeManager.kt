package com.demo.goingapplock.vpn

import com.demo.goingapplock.interfaces.IConnectTimeInterface
import kotlinx.coroutines.*
import java.lang.Exception

object TimeManager {
    private var time=0L
    private var job:Job?=null
    private var iConnectTimeInterface:IConnectTimeInterface?=null

    fun setInterface(iConnectTimeInterface:IConnectTimeInterface?){
        this.iConnectTimeInterface=iConnectTimeInterface
    }

    fun reset(){
        time=0L
    }

    fun startTime(){
        if (null!= job) return
        job = GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                iConnectTimeInterface?.connectTime(transTime(time))
                time++
                delay(1000L)
            }
        }
    }

    fun endTime(){
        job?.cancel()
        job=null
    }

    fun getTotalTimeStr()= transTime(time)

    private fun transTime(t:Long):String{
        try {
            val shi=t/3600
            val fen= (t % 3600) / 60
            val miao= (t % 3600) % 60
            val s=if (shi<10) "0${shi}" else shi
            val f=if (fen<10) "0${fen}" else fen
            val m=if (miao<10) "0${miao}" else miao
            return "${s}:${f}:${m}"
        }catch (e: Exception){}
        return "00:00:00"
    }
}
