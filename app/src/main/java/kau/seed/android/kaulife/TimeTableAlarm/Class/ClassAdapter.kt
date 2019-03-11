package kau.seed.android.kaulife.TimeTableAlarm.Class

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.orm.SugarRecord
import kau.seed.android.kaulife.R


class ClassAdapter (var context : ClassListActivity, var classes : ArrayList<ClassData>) : RecyclerView.Adapter<ClassAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_class, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return classes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textClassInfo.text = classes[position].changeToInfo()
        holder.textTime.text = classes[position].time
        holder.view.setOnClickListener {
            var result = true
            var classData = classes[position]
            var classDatas : ArrayList<ClassData> = SugarRecord.listAll(ClassData::class.java) as ArrayList<ClassData>
            for (schedule in classDatas) {
                if (classData.major == schedule.major &&
                        classData.professor == schedule.professor &&
                        classData.time == schedule.time)
                {
                    result = false
                    break
                }
            }
            if (context.selected.size + classDatas.size >= 10) {
                result = false
            }
            if (result) {
                if (classData in context.selected) {
                    result = false
                }
            }

            if (result) {
                result = judgeTime(classDatas, classData)
            }

            if (result) {
                result = judgeTime(context.selected, classData)
            }

            if (result) {
                context.selected.add(0, classData)
                context.selectedAdapter.notifyDataSetChanged()
                var toast = Toast.makeText(context, "과목이 목록에 추가되었습니다. 나머지 과목들도 선택하신 후 저장을 눌러주세요.", Toast.LENGTH_LONG)
                toast.show()
            } else {
                var toast = Toast.makeText(context, "이미 등록되어 있거나, 시간이 겹치는 과목입니다.", Toast.LENGTH_LONG)
                toast.show()
            }

        }

    }

    class ViewHolder (val view : View) : RecyclerView.ViewHolder(view) {
        val textClassInfo : TextView = view.findViewById(R.id.textClassInfo)
        val textTime : TextView = view.findViewById(R.id.textTime)

    }

    fun judgeTime (classDatas : ArrayList<ClassData>, classData : ClassData) : Boolean {
        var timeIntList : ArrayList<Int> = arrayListOf()
        for (classData in classDatas) {
            var time = classData.time
            var timeList = time.split("  ")
            var timeInt = 0
            for (time in timeList) { // time 화)10:00∼13:00
                when (time[0].toString()) {
                    "일" -> timeInt = 1
                    "월" -> timeInt = 2
                    "화" -> timeInt = 3
                    "수" -> timeInt = 4
                    "목" -> timeInt = 5
                    "금" -> timeInt = 6
                    "토" -> timeInt = 7
                }

                timeInt *= 10000

                var timeTemp = time.subSequence(2, time.length) // timeTemp = 10:00∼13:00
                var timeStart = (timeTemp.subSequence(0,2).toString() + timeTemp.subSequence(3,5).toString()).toInt() + timeInt
                var timeEnd = (timeTemp.subSequence(6,8).toString() + timeTemp.subSequence(9, 11).toString()).toInt() + timeInt


                while (timeStart < timeEnd) {
                    timeIntList.add(timeStart)
                    timeStart = timeStart + 30
                    if (timeStart % 100 == 60) {
                        timeStart = timeStart + 40
                    }
                }

            }
        }
        var timeInput = classData.time
        var timeListInput = timeInput.split("  ")
        var timeIntInput = 0
        for (time in timeListInput) {
            when (time[0].toString()) {
                "일" -> timeIntInput = 1
                "월" -> timeIntInput = 2
                "화" -> timeIntInput = 3
                "수" -> timeIntInput = 4
                "목" -> timeIntInput = 5
                "금" -> timeIntInput = 6
                "토" -> timeIntInput = 7
            }

            timeIntInput *= 10000

            var timeTempInput = time.subSequence(2, time.length) // timeTemp = 10:00∼13:00
            var timeStartInput = (timeTempInput.subSequence(0, 2).toString() + timeTempInput.subSequence(3, 5).toString()).toInt() + timeIntInput
            var timeEndInput = (timeTempInput.subSequence(6, 8).toString() + timeTempInput.subSequence(9, 11).toString()).toInt() + timeIntInput


            while (timeStartInput < timeEndInput) {
                if (timeStartInput in timeIntList) {
                    return false
                }
                timeStartInput = timeStartInput + 30
                if (timeStartInput % 100 == 60) {
                    timeStartInput = timeStartInput + 40
                }
            }
        }

        return true
    }
}