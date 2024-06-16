package com.example.emailtask.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emailtask.R
import com.example.emailtask.model.Contact
import java.util.Collections

class ScheduleMembersAdapter(
    private var members: MutableList<Contact>,
    private val startDragListener: (viewHolder: RecyclerView.ViewHolder) -> Unit,
) : RecyclerView.Adapter<ScheduleMembersAdapter.ItemViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.schedule_member_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: String = members[position].name
        holder.textView.text = item
        holder.handleView.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                startDragListener(holder)
            }
            false
        }
    }

    override fun getItemCount(): Int = members.size

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        //memberMoveListener(fromPosition, toPosition)
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(members, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(members, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun updateMembers(newMembers: List<Contact>) {
        members = newMembers.toMutableList()
        notifyDataSetChanged()
    }

    fun getMembers(): List<Contact> {
        return members.toList()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvGroupMemberItemName)
        val handleView: ImageView = itemView.findViewById(R.id.groupMemberItemHandle)
    }
}