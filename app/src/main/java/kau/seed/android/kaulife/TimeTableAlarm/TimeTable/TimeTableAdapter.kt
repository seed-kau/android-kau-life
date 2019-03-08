package kau.seed.android.kaulife.TimeTableAlarm.TimeTable

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kau.seed.android.kaulife.R

class TimeTableAdapter (var context : TimeTableActivity, var display : ArrayList<TimeTableData>) : RecyclerView.Adapter<TimeTableAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TimeTableAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_timetable, parent, false)

        return TimeTableAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return display.size
    }

    override fun onBindViewHolder(holder: TimeTableAdapter.ViewHolder, position: Int) {
        holder.textTableSubject.text = display[position].subject
        holder.textTableRoom.text = display[position].room
        holder.view.setBackgroundColor(display[position].color)
    }

    class ViewHolder (val view : View) : RecyclerView.ViewHolder(view) {
        val textTableSubject : TextView = view.findViewById(R.id.textTableSubject)
        val textTableRoom : TextView = view.findViewById(R.id.textTableRoom)
    }
}