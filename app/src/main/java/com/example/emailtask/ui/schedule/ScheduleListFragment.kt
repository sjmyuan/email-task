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
import com.example.emailtask.adapter.ScheduleAdapter
import com.example.emailtask.model.AppViewModel
import com.example.emailtask.model.RecurrenceType
import com.example.emailtask.model.Schedule
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ScheduleListFragment : Fragment() {

    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.lvSchedules)

        val adapter = ScheduleAdapter(listOf(), listOf()) { schedule ->
            appViewModel.setEditingSchedule(schedule)
            val navController = findNavController()
            navController.navigate(R.id.action_to_schedule_details)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val newSchedule = view.findViewById<Button>(R.id.btnNewSchedule)
        newSchedule.setOnClickListener {
            val currentMoment: Instant = Clock.System.now()
            val now: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

            appViewModel.setEditingSchedule(
                Schedule(
                    System.currentTimeMillis(),
                    "", listOf(), listOf(), listOf(),
                    now, RecurrenceType.NOT_REPEAT, ""
                )
            )

            val navController = findNavController()
            navController.navigate(R.id.action_to_schedule_details)
        }

        appViewModel.schedulesAndContacts.observe(viewLifecycleOwner) { data ->
            data?.let {
                adapter.updateData(
                    it.first ?: listOf(),
                    it.second ?: listOf(),
                )
            }
        }
    }
}