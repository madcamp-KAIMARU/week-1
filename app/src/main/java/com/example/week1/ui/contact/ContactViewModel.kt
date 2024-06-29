package com.example.week1.ui.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Contact(val name: String, val number: String)

class ContactViewModel : ViewModel() {

    // init
//    private val _text = MutableLiveData<String>().apply {
//        value = "This is contact Fragment"
//    }
//    val text: LiveData<String> = _text

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> get() = _contacts

    fun setContacts(contactList: List<Contact>) {
        _contacts.value = contactList
    }
}