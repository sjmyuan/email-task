package com.example.emailtask.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emailtask.R
import com.example.emailtask.adapter.EventAdapter
import com.example.emailtask.model.AppViewModel

class EventListFragment : Fragment() {

    private val appViewModel: AppViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_message_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.lvMessages)

        val adapter = EventAdapter(listOf(), listOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        appViewModel.schedulesAndContacts.observe(viewLifecycleOwner) {
            it?.let { (schedules, contacts) ->
                schedules?.flatMap { schedule ->
                    schedule.pendingEvents.map { event ->
                        Pair(
                            schedule.name,
                            event
                        )
                    }
                }?.let { events ->
                    adapter.updateEvents(
                        events.sortedBy { event -> event.second.sentTime },
                        contacts ?: listOf()
                    )
                }
            }
        }
    }
}