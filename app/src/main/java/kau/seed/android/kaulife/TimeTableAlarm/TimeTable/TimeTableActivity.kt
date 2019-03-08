package kau.seed.android.kaulife.TimeTableAlarm.TimeTable

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import com.orm.SugarRecord
import kau.seed.android.kaulife.R
import kau.seed.android.kaulife.TimeTableAlarm.Class.ClassData
import kau.seed.android.kaulife.TimeTableAlarm.Class.ClassListActivity
import kau.seed.android.kaulife.TimeTableAlarm.TimeTableAlarmReceiver
import kotlinx.android.synthetic.main.activity_time_table.*

class TimeTableActivity : AppCompatActivity() {


    private lateinit var timeTables : ArrayList<ClassData>
    private var display = ArrayList<TimeTableData>()
    private lateinit var colors : ArrayList<Int>
    private lateinit var timeTableAdapter : TimeTableAdapter
    private lateinit var timeTableLayoutManager : RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_time_table)

        initModel()
        setCustomActionBar()
        initRecycler()
        initDisplay()
        aboutView()

    }

    override fun onResume() {
        super.onResume()

        initModel()
        setCustomActionBar()
        initRecycler()
        initDisplay()
        aboutView()
    }

    private fun initModel () {
        colors = ArrayList()
        colors.add(Color.rgb(234,133,188))
        colors.add(Color.rgb(233,196,106))
        colors.add(Color.rgb(162,194,106))
        colors.add(Color.rgb(216,162,212))
        colors.add(Color.rgb(122,161,220))
        colors.add(Color.rgb(245,164,101))
        colors.add(Color.rgb(129,209,191))
        colors.add(Color.rgb(217,115,247))
        colors.add(Color.rgb(145,247,115))
        colors.add(Color.rgb(115,145,247))

        timeTables = SugarRecord.listAll(ClassData::class.java) as ArrayList<ClassData>
    }

    private fun setCustomActionBar() {
        val actionBar = supportActionBar

        actionBar!!.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setDisplayShowTitleEnabled(false)

        val actionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_default, null)
        actionBar.customView = actionBarView

        val params = ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        actionBar.setCustomView(actionBarView, params)
    }

    private fun initRecycler () {
        timeTableAdapter = TimeTableAdapter(this, display)
        timeTableLayoutManager = GridLayoutManager (this,6)
        recyclerTimeTable.apply {
            setHasFixedSize(true)
            adapter = timeTableAdapter
            layoutManager = timeTableLayoutManager
        }
    }

    private fun initDisplay () {
        display.clear()
        display.add(TimeTableData("", ""))
        display.add(TimeTableData("월", ""))
        display.add(TimeTableData("화", ""))
        display.add(TimeTableData("수", ""))
        display.add(TimeTableData("목", ""))
        display.add(TimeTableData("금", ""))

        var tableIndex = 0
        for (classData in timeTables) {
            var mode = 0

            var indexs = classData.changeToIndex()
            for (i in indexs) {
                var type = 0
                for (j in i) {
                    addDisplay (j, classData, mode, type, tableIndex)
                    type = 1
                }
                mode += 1
            }
            tableIndex += 1
        }

        timeTableAdapter.notifyDataSetChanged()

        var index = 6
        var time = 9
        while (index < display.size) {
            display[index].subject = time.toString()
            index += 12
            time += 1
        }
    }

    private fun addDisplay (index : Int, classData : ClassData, mode : Int, type : Int, tableIndex : Int) {
        val listRoom = classData.room.split(" / ")
        if (index > display.size) {
            while (true) {
                display.add(TimeTableData ("", ""))
                if (display.size > index && display.size % 6 == 0) {
                    break
                }
            }
        }
        if (type == 0 ) {
            display[index].subject = classData.subject
            display[index].room = listRoom[mode]
        }

        display[index].color = colors[tableIndex]

    }

    private fun aboutView () {
        fabAdd.setOnClickListener {
            var intent = Intent (this, ClassListActivity::class.java)
            startActivity(intent)
        }

        fabClear.setOnClickListener {
            openDialog()
        }
    }

    fun openDialog () {
        var builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("확인")
        builder.setMessage("시간표를 초기화 시키시겠습니까 ?")

        builder.setPositiveButton("확인"){dialog, which ->
            SugarRecord.deleteAll(ClassData::class.java)
            for (classData in timeTables) {
                alarmCancel(classData)
            }
            timeTables.clear()
            initDisplay()

            initModel()
            timeTableAdapter.notifyDataSetChanged()

            //알람 전체 삭제 들어가야함
        }
        builder.setNegativeButton("취소") {dialog, which ->

        }

        var alertDialog : AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun alarmCancel (classData : ClassData) {
        var alarmManager : AlarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var dataId = classData.id
        var times = classData.time.split("  ")
        for (time in times) {
            var intent = Intent (this, TimeTableAlarmReceiver::class.java)
            var pending = PendingIntent.getBroadcast(this, dataId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
            if (pending != null) {
                alarmManager.cancel(pending)
                pending.cancel()
            }
            dataId += 1000
        }
    }
}
