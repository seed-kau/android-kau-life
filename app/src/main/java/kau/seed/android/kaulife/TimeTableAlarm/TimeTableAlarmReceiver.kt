package kau.seed.android.kaulife.TimeTableAlarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.orm.SugarRecord
import kau.seed.android.kaulife.TimeTableAlarm.Class.ClassData
import java.util.*

class TimeTableAlarmReceiver : BroadcastReceiver() {
    private var extra: Bundle? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationHelper = TimeTableNoti(context)
        extra = intent?.extras
        var id = extra?.getLong("id")
        println (id)
        if (checkDay(id)) {
            println("notification")
            notificationHelper.initView()
            if (id != null) {
                notificationHelper.generateAlarm(id % 1000)
            }
            println ("finish")
        }
    }

    private fun checkDay (id : Long?) : Boolean{
        var subId : Long = id!! % 1000
        var calendar = Calendar.getInstance()
        var day = calendar.get(Calendar.DAY_OF_WEEK)
        var dayString = ""
        var dataDay = ""
        when (day) {
            1 -> dayString = "일"
            2 -> dayString = "월"
            3 -> dayString = "화"
            4 -> dayString = "수"
            5 -> dayString = "목"
            6 -> dayString = "금"
            7 -> dayString = "토"
        }


        if (id != null) {
            val data = SugarRecord.findById(ClassData::class.java, subId)
            val dataId = data.id
            if (dataId == id) {
                dataDay = data.time[0].toString()
                return dataDay == dayString
            } else if (dataId + 1000 == id) {
                var timeTemp = data.time.split("  ")
                dataDay = timeTemp[1][0].toString()
                return dataDay == dayString
            }
        }
        return false
    }
}