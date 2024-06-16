package com.example.emailtask.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class DropDownItemAdapter<T>(
    private val context: Context,
    private var items: List<T>,
    private val getItemName: (item: T) -> String
) : ArrayAdapter<T>(context, android.R.layout.simple_spinner_item, items) {
    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = getItem(position)?.let(getItemName)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = getItem(position)?.let(getItemName)
        return view
    }
}