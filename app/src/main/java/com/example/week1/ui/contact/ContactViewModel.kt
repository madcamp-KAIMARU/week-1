package com.example.week1.ui.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Contact(
    val name: String,
    val number: String,
    val isHeader: Boolean = false,
    val initial: Char = if (name.isNotEmpty()) name.first().toUpperCase() else ' '
)

class ContactViewModel : ViewModel() {
    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> get() = _contacts

    fun setContacts(contactList: List<Contact>) {
        _contacts.value = contactList
    }
}