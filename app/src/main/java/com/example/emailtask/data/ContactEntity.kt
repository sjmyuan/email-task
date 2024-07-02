package com.example.emailtask.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.emailtask.model.Contact

@Entity(tableName = "contact")
data class ContactEntity(
    @PrimaryKey val contactId: Long,
    val name: String,
    val mobile: String,
    val email: String,
) {
    fun toContact() = Contact(contactId, name, mobile, email)
    fun fromContact(contact: Contact) =
        ContactEntity(contact.id, contact.name, contact.mobile, contact.email)
}
