package com.example.emailtask.repository

import androidx.annotation.WorkerThread
import com.example.emailtask.data.ContactDao
import com.example.emailtask.data.ContactEntity
import com.example.emailtask.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ContactRepository(private val contactDao: ContactDao) {
    val allContacts: Flow<List<Contact>> =
        contactDao.getAll().map { contactEntities -> contactEntities.map { it.toContact() } }

    @WorkerThread
    suspend fun insert(contact: Contact) {
        contactDao.insertContacts(ContactEntity.fromContact(contact))
    }

    @WorkerThread
    suspend fun delete(contact: Contact) {
        contactDao.deleteContacts(ContactEntity.fromContact(contact))
    }
}
