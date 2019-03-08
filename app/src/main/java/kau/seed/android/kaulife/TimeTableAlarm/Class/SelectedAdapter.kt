package kau.seed.android.kaulife.TimeTableAlarm.Class

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kau.seed.android.kaulife.R
import kotlinx.android.synthetic.main.item_selected.view.*

class SelectedAdapter (var context : ClassListActivity, var selected : ArrayList<ClassData>) : RecyclerView.Adapter<SelectedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): SelectedAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_selected, parent, false)

        return SelectedAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return selected.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.buttonDelete.setOnClickListener {
            selected.removeAt(position)
            context.selectedAdapter.notifyDataSetChanged()
        }

        holder.textViewSubject.text = selected[position].subject
        holder.textViewProfessor.text = selected[position].professor
    }

    class ViewHolder (val view : View) : RecyclerView.ViewHolder(view) {
        val textViewSubject : TextView = view.findViewById(R.id.textSubject)
        val textViewProfessor : TextView = view.findViewById(R.id.textProfessor)
        val buttonDelete : Button = view.findViewById(R.id.buttonDelete)
    }
}