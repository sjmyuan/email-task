package com.example.emailtask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.emailtask.R
import com.example.emailtask.model.Contact
import com.example.emailtask.model.Event
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format

class EventAdapter(
    private var events: List<Pair<String, Event>>,
    private var contacts: List<Contact>,
) : RecyclerView.Adapter<EventAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = events[position]
        val contact: Contact? = contacts.find { it.id == event.second.receiver }

        val scheduleName: TextView = holder.itemView.findViewById(R.id.tvMessageItemScheduleName)
        val receiverName: TextView = holder.itemView.findViewById(R.id.tvMessageItemReceiverName)
        val mobile: TextView = holder.itemView.findViewById(R.id.tvMessageItemMobile)
        val email: TextView = holder.itemView.findViewById(R.id.tvMessageItemEmail)
        val message: TextView = holder.itemView.findViewById(R.id.tvMessageItemMessage)
        val dateTime: TextView = holder.itemView.findViewById(R.id.tvMessageItemTime)

        contact?.let {
            scheduleName.text = event.first
            receiverName.text = it.name
            mobile.text = it.mobile
            email.text = it.email
            message.text = event.second.message
            dateTime.text = event.second.sentTime.format(LocalDateTime.Formats.ISO)
        }
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<Pair<String, Event>>, newContacts: List<Contact>) {
        events = newEvents
        contacts = newContacts
        notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : ViewHolder(itemView) {}
}