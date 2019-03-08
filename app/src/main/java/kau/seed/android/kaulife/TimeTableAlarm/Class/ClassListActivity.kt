package kau.seed.android.kaulife.TimeTableAlarm.Class

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kau.seed.android.kaulife.R
import kau.seed.android.kaulife.Server.ServerConstructor
import kau.seed.android.kaulife.TimeTableAlarm.TimeTableAlarmReceiver
import kotlinx.android.synthetic.main.activity_class_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ClassListActivity : AppCompatActivity() {

    var classes = ArrayList<ClassData>()
    var display = ArrayList<ClassData>()
    var selected = ArrayList<ClassData>()
    val serverInterface = ServerConstructor().getService()

    var alarmManager : AlarmManager? = null

    lateinit var classAdapter : ClassAdapter
    lateinit var classLayoutManager : RecyclerView.LayoutManager

    lateinit var selectedAdapter : SelectedAdapter
    lateinit var selectedLayoutManager : RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_list)

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        setCustomActionBar()
        initRecycler()
        initData()
        aboutView()
    }

    private fun initData () {
        val serverHandler : Call<ArrayList<ClassData>> = serverInterface.getClass()
        serverHandler.enqueue(object : Callback<ArrayList<ClassData>> {
            override fun onResponse(call: Call<ArrayList<ClassData>>, response: Response<ArrayList<ClassData>>) {
                if (response.body() != null) {
                    classes.addAll(response.body()!!)
                    display.addAll(response.body()!!)
                    classAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ArrayList<ClassData>>, t: Throwable) {
                Toast.makeText(applicationContext, "잠시 후 다시 시도해 주세요", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initRecycler () {
        classAdapter = ClassAdapter (this, display)
        classLayoutManager = LinearLayoutManager(this)
        recyclerClass.apply {
            setHasFixedSize(true)
            layoutManager = classLayoutManager
            adapter = classAdapter
        }

        selectedAdapter = SelectedAdapter (this, selected)
        selectedLayoutManager = LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        recyclerSelected.apply {
            setHasFixedSize(true)
            layoutManager = selectedLayoutManager
            adapter = selectedAdapter
        }
    }

    private fun aboutView () {
        spinnerMajor.adapter = ArrayAdapter.createFromResource(this, R.array.major, android.R.layout.simple_spinner_item)
        spinnerMajor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var major = spinnerMajor.getItemAtPosition(position).toString()
                display.clear()
                if (major == "전체") {
                    display.addAll(classes)
                } else {
                    for (classData in classes) {
                        if (classData.major == major) {
                            display.add(classData)
                        }
                    }
                }

                recyclerClass.adapter!!.notifyDataSetChanged()
            }
        }



        buttonSave.setOnClickListener {
            for(classData in selected) {
                classData.save()
            }

            for (item in selected) {
                setAlarm(item)
            }

            finish()
        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                display.clear()
                for (classData in classes) {
                    if (p0.toString() in classData.subject) {
                        display.add(classData)
                    }
                }
                recyclerClass.adapter!!.notifyDataSetChanged()
            }
        })
    }

    private fun setAlarm (classData : ClassData) {
        var id = classData.id
        val time = classData.time
        val timeList = time.split("  ")
        for (i in timeList) {
            val triggerTime = setTriggerTime (i)
            val alarmIntent = Intent(this, TimeTableAlarmReceiver::class.java)
            alarmIntent.putExtra("id", id)
            val pending = PendingIntent.getBroadcast(this, id.toInt(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager?.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, 86400000, pending)
            id += 1000
        }
    }

    private fun setTriggerTime(time: String): Long {
        //"금)09:00∼11:00"
        val timeOrigin = time.slice(2 until time.length)
        val timeStart = timeOrigin.split("∼")
        val timeList = timeStart[0].split(":")
        val timeHour = timeList[0].toInt()
        val timeMinute = timeList[1].toInt()

        val currentTime = System.currentTimeMillis()
        val registerTime = Calendar.getInstance()

        registerTime.set(Calendar.HOUR_OF_DAY, timeHour)
        registerTime.set(Calendar.MINUTE, timeMinute - 10)
        registerTime.set(Calendar.SECOND, 0)
        registerTime.set(Calendar.MILLISECOND, 0)

        var triggerTime = registerTime.timeInMillis

        if (currentTime > triggerTime) {
            triggerTime += 86400000
        }

        return triggerTime
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
}
