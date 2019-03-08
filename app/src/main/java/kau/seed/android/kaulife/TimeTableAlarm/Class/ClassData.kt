package kau.seed.android.kaulife.TimeTableAlarm.Class

import com.orm.SugarRecord

class ClassData (var subject : String= "", var professor : String = "", var major : String = "", var time : String = "", var room : String = "") : SugarRecord(){

    fun changeToInfo () : String {
        var result = ""
        result += "$subject "
        result += "$professor "
        result += "$room "

        return result
    }

    fun changeToIndex () : ArrayList<ArrayList<Int>> {
        var result : ArrayList<ArrayList<Int>> = ArrayList()
        var timeList = time.split("  ")
        for (i in timeList) {
            var temp = ArrayList<Int> ()
            var day = i.substring(0, 1)
            var extra = 0
            extra = when (day) {
                "월" -> 1
                "화" -> 2
                "수" -> 3
                "목" -> 4
                "금" -> 5
                else -> 0
            }
            var hour = i.substring(2, 4).toInt()
            var minute = i.substring(5, 7).toInt()

            var finishHour = i.substring(8, 10).toInt()
            var finishMinute = i.substring(11, 13).toInt()

            while (true) {
                var index = (hour - 9) * 2 + 1
                if (minute == 30) {
                    index += 1
                }
                index *= 6
                index += extra
                temp.add(index)

                minute += 30
                if (minute == 60) {
                    minute = 0
                    hour += 1
                }

                if (hour == finishHour && minute == finishMinute) {
                    break
                }
            }
            result.add(temp)
        }

        return result
    }
}