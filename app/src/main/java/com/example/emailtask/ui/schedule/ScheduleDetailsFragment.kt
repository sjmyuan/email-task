package com.example.emailtask.ui.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emailtask.R
import com.example.emailtask.adapter.ContactTouchHelper
import com.example.emailtask.adapter.DropDownItemAdapter
import com.example.emailtask.adapter.ScheduleMembersAdapter
import com.example.emailtask.model.AppViewModel
import com.example.emailtask.model.Contact
import com.example.emailtask.model.RecurrenceType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import java.util.Calendar

class ScheduleDetailsFragment : Fragment() {

    private val appViewModel: AppViewModel by activityViewModels()
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.schedule_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editName: EditText = view.findViewById(R.id.tvScheduleName)
        editName.doAfterTextChanged { appViewModel.updateScheduleName(it.toString()) }

        val editMessage: EditText = view.findViewById(R.id.tvScheduleMessage)
        editMessage.doAfterTextChanged { appViewModel.updateScheduleMessage(it.toString()) }

        val editDate: EditText = view.findViewById(R.id.tvScheduleDate)
        editDate.doAfterTextChanged {
            appViewModel.updateScheduleDate(
                LocalDate.Formats.ISO.parse(it.toString()),
            )
        }
        editDate.setOnClickListener {
            showDatePickerDialog(requireContext(), editDate)
        }


        val editTime: EditText = view.findViewById(R.id.tvScheduleTime)
        editTime.doAfterTextChanged {
            appViewModel.updateScheduleTime(
                LocalTime.Formats.ISO.parse(it.toString())
            )
        }
        editTime.setOnClickListener {
            showTimePickerDialog(requireContext(), editTime)
        }

        val scheduleRecurrenceAdapter = DropDownItemAdapter<RecurrenceType>(
            requireContext(), RecurrenceType.entries.toList()
        ) { recurrenceType ->
            recurrenceType.description
        }
        scheduleRecurrenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val scheduleRecurrence: Spinner = view.findViewById(R.id.spnScheduleRecurrence)
        scheduleRecurrence.adapter = scheduleRecurrenceAdapter
        scheduleRecurrence.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position) as RecurrenceType
                appViewModel.updateScheduleRecurrence(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


        val scheduleMembersAdapter = ScheduleMembersAdapter(
            mutableListOf()
        ) { viewHolder -> itemTouchHelper.startDrag(viewHolder) }
        val scheduleMembers: RecyclerView = view.findViewById(R.id.rvScheduleMembers)
        scheduleMembers.adapter = scheduleMembersAdapter
        scheduleMembers.layoutManager = LinearLayoutManager(requireContext())
        val callback = ContactTouchHelper(
            scheduleMembersAdapter
        ) { ->
            appViewModel.updateScheduleReceivers(scheduleMembersAdapter.getMembers().map { it.id })
        }
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(scheduleMembers)

        val editScheduleMember = view.findViewById<Button>(R.id.editScheduleMember)
        editScheduleMember.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_from_schedule_details_to_schedule_member_editor)
        }

        val btnSave: Button = view.findViewById(R.id.btnScheduleSave)
        btnSave.setOnClickListener {
            appViewModel.saveEditingSchedule()
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        appViewModel.editingScheduleAndContacts.observe(viewLifecycleOwner) { data ->

            data?.let {
                val currentSchedule = data.first
                val contacts = data.second
                if (currentSchedule != null && contacts != null) {

                    if (editName.text.toString() != currentSchedule.name) {
                        editName.setText(currentSchedule.name)
                    }

                    if (editMessage.text.toString() != currentSchedule.message) {
                        editMessage.setText(currentSchedule.message)
                    }

                    editDate.setText(currentSchedule.sentTime.date.format(LocalDate.Formats.ISO))
                    editTime.setText(currentSchedule.sentTime.time.format(LocalTime.Formats.ISO))
                    scheduleRecurrence.setSelection(currentSchedule.recurrence.ordinal)
                    scheduleMembersAdapter.updateMembers(
                        currentSchedule.receivers.mapNotNull { contacts.find { contact -> contact.id == it } },
                    )
                }
            }
        }
    }

    private fun showDatePickerDialog(context: Context, editTextDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                val date =
                    String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                editTextDate.setText(date)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(context: Context, editTextTime: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            val time = String.format("%02d:%02d:00", selectedHour, selectedMinute)
            editTextTime.setText(time)
        }, hour, minute, true)

        timePickerDialog.show()
    }
}
