package com.example.emailtask.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emailtask.R
import com.example.emailtask.adapter.ScheduleMembersEditorAdapter
import com.example.emailtask.model.AppViewModel

class ScheduleMembersEditorFragment : Fragment() {

    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.schedule_member_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.lvScheduleMemberEditor)
        val btnSave = view.findViewById<Button>(R.id.btnScheduleEditor)

        val adapter = ScheduleMembersEditorAdapter(
            listOf(), listOf()
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        btnSave.setOnClickListener {
            appViewModel.updateScheduleReceivers(adapter.getMembers())
            findNavController().navigateUp()
        }

        appViewModel.editingScheduleAndContacts.observe(viewLifecycleOwner) { scheduleAndContacts ->
            val schedule = scheduleAndContacts.first
            val contacts = scheduleAndContacts.second
            if (schedule != null && contacts != null) {
                adapter.updateMembersAndContacts(schedule.receivers, contacts)
            }
        }

    }
}