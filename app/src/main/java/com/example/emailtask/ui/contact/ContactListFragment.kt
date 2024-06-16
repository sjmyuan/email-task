package com.example.emailtask.ui.contact

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
import com.example.emailtask.adapter.ContactAdapter
import com.example.emailtask.model.AppViewModel
import com.example.emailtask.model.Contact

class ContactListFragment : Fragment() {

    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.lvContacts)

        val adapter = ContactAdapter(
            listOf(), appViewModel::moveContact
        ) { contact ->
            appViewModel.setEditingContact(contact)
            val navController = findNavController()
            navController.navigate(R.id.action_to_contact_details)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        appViewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            adapter.updateContacts(contacts)
        }

        val newContractBtn = view.findViewById<Button>(R.id.btnNewContact)
        newContractBtn.setOnClickListener {
            appViewModel.setEditingContact(
                Contact(
                    System.currentTimeMillis(), "", "", ""
                )
            )
            val navController = findNavController()
            navController.navigate(R.id.action_to_contact_details)
        }
    }
}