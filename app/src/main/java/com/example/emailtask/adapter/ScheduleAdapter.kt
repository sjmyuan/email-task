package com.example.emailtask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emailtask.R
import com.example.emailtask.model.Contact
import com.example.emailtask.model.Schedule

class ScheduleAdapter(
    private var schedules: List<Schedule>,
    private var contacts: List<Contact>,
    private val scheduleClickListener: (schedule: Schedule) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: Schedule = schedules[position]

        holder.textView.text = item.name

        holder.itemView.setOnClickListener {
            scheduleClickListener(item)
        }
    }

    override fun getItemCount(): Int = schedules.size

    fun updateData(
        newSchedules: List<Schedule>, newContacts: List<Contact>
    ) {
        schedules = newSchedules
        contacts = newContacts
        notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvScheduleItemName)
    }
}