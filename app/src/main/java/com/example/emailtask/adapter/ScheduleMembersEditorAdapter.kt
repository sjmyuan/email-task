package com.example.emailtask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emailtask.R
import com.example.emailtask.model.Contact

class ScheduleMembersEditorAdapter(
    private var members: List<Long>,
    private var contacts: List<Contact>
) : RecyclerView.Adapter<ScheduleMembersEditorAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_member_editor_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: Contact = contacts[position]
        holder.textView.text = item.name
        holder.stateView.isChecked = members.find { it == item.id } != null
        holder.stateView.setOnCheckedChangeListener { _, isChecked ->
            members = if (isChecked) {
                members + item.id
            } else {
                members.filter { it != item.id }
            }
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun updateMembersAndContacts(newMembers: List<Long>, newContacts: List<Contact>) {
        members = newMembers
        contacts = newContacts
        notifyDataSetChanged()
    }

    fun getMembers(): List<Long> {
        return members
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvGroupMemberEditorItemName)
        val stateView: CheckBox = itemView.findViewById(R.id.groupMemberEditorItemState)
    }
}