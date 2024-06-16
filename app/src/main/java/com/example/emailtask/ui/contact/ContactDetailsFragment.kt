package com.example.emailtask.ui.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.emailtask.R
import com.example.emailtask.model.AppViewModel
import com.example.emailtask.model.Contact

class ContactDetailsFragment : Fragment() {

    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.contact_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editName: EditText = view.findViewById(R.id.tvContactDetailsName)
        val editEmail: EditText = view.findViewById(R.id.tvContactDetailsEmail)
        val editMobile: EditText = view.findViewById(R.id.tvContactDetailsMobile)
        val btnSave: Button = view.findViewById(R.id.btnContactDetailsSave)

        appViewModel.editingContact.observe(viewLifecycleOwner) { contact ->
            contact?.let { editingContact ->
                editName.setText(editingContact.name)
                editEmail.setText(editingContact.email)
                editMobile.setText(editingContact.mobile)
                btnSave.setOnClickListener {
                    appViewModel.updateContact(
                        editingContact.copy(
                            name = editName.text.toString(),
                            mobile = editMobile.text.toString(),
                            email = editEmail.text.toString()
                        )
                    )
                    findNavController().navigateUp()
                }
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })


    }
}
