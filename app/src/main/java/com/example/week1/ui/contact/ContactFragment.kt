package com.example.week1.ui.contact

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week1.R
import com.example.week1.databinding.FragmentContactBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class ContactFragment : Fragment(), AddContactDialogFragment.OnContactAddedListener, EditContactDialogFragment.OnContactEditedListener {
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactViewModel: ContactViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var allContacts: MutableList<Contact>
    private lateinit var contactAdapter: ContactAdapter

    private var isFabMenuOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        val root: View = binding.root

        contactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)
        sharedPreferences = requireContext().getSharedPreferences("contacts", Context.MODE_PRIVATE)

        val recyclerView = binding.contactRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        contactViewModel.contacts.observe(viewLifecycleOwner, Observer { contacts ->
            contacts?.let {
                val sortedContacts = addHeaders(it)
                contactAdapter = ContactAdapter(requireContext(), sortedContacts, { contact ->
                    deleteContact(contact)
                }, { contact ->
                    editContact(contact)
                })
                recyclerView.adapter = contactAdapter
            }
        })

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                1
            )
        } else {
            synchronizeContacts()
        }

        val fabMain: FloatingActionButton = binding.fabMain
        val fabSync: FloatingActionButton = binding.fabSync
        val fabAdd: FloatingActionButton = binding.fabAdd

        fabMain.setOnClickListener {
            if (isFabMenuOpen) {
                closeFabMenu(fabSync, fabAdd, fabMain)
            } else {
                openFabMenu(fabSync, fabAdd, fabMain)
            }
        }

        fabSync.setOnClickListener {
            synchronizeContacts()
            Toast.makeText(requireContext(), "Contacts synchronized", Toast.LENGTH_SHORT).show()
            closeFabMenu(fabSync, fabAdd, fabMain)
        }

        fabAdd.setOnClickListener {
            val dialog = AddContactDialogFragment()
            dialog.setOnContactAddedListener(this)
            dialog.show(parentFragmentManager, " AddContactDialogFragment ")
            closeFabMenu(fabSync, fabAdd, fabMain)
        }
        val searchBar = binding.searchBar
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return root
    }

    private fun openFabMenu(
        fabSync: FloatingActionButton,
        fabAdd: FloatingActionButton,
        fabMain: FloatingActionButton
    ) {
        isFabMenuOpen = true
        fabSync.visibility = View.VISIBLE
        fabAdd.visibility = View.VISIBLE
        fabMain.setImageResource(R.drawable.ic_close)
    }

    private fun closeFabMenu(
        fabSync: FloatingActionButton,
        fabAdd: FloatingActionButton,
        fabMain: FloatingActionButton
    ) {
        isFabMenuOpen = false
        fabSync.visibility = View.GONE
        fabAdd.visibility = View.GONE
        fabMain.setImageResource(R.drawable.ic_open)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("ContactSync", "Permission granted")
            loadContacts()
        } else {
            Log.d("ContactSync", "Permission denied")
        }
    }

    private fun loadContacts() {
        val contactList = mutableListOf<Contact>()

        val savedContactsJson = sharedPreferences.getString("saved_contacts", "")
        if (!savedContactsJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<Contact>>() {}.type
            val savedContacts: List<Contact> = Gson().fromJson(savedContactsJson, type)
            contactList.addAll(savedContacts)
        }

        contactList.sortBy { it.name }

        allContacts = contactList
        contactViewModel.setContacts(contactList)
    }

    private fun synchronizeContacts() {
        if (!::allContacts.isInitialized) {
            Log.e("synchronizeContacts", "allContacts has not been initialized")
            loadContacts()
        }

        Log.d("ContactSync", "Starting synchronization")
        val currentContacts = allContacts.toMutableList()
        val contactList = mutableListOf<Contact>()

        Log.d("ContactSync", "Loading contacts from phone")
        val resolver = requireContext().contentResolver
        val cursor: Cursor? = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val number = cursor.getString(numberIndex)
                val initial = if (name.isNotEmpty()) name.first().uppercaseChar() else ' '
                val contact = Contact(name, number, false, initial)
                if (!contactList.any { it.name == contact.name && it.number == contact.number }) {
                    contactList.add(contact)
                }
            }
        }
        Log.d("ContactSync", "Loaded ${contactList.size} contacts from phone")

        Log.d("ContactSync", "Loading contacts from SharedPreferences")
        val savedContactsJson = sharedPreferences.getString("saved_contacts", "")
        if (!savedContactsJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<Contact>>() {}.type
            val savedContacts: List<Contact> = Gson().fromJson(savedContactsJson, type)
            for (contact in savedContacts) {
                if (!contactList.any { it.name == contact.name && it.number == contact.number }) {
                    contactList.add(contact)
                }
            }
        }
        Log.d(
            "ContactSync",
            "Loaded ${contactList.size} contacts after merging with saved contacts"
        )

        contactList.sortBy { it.name }

        allContacts = contactList
        contactViewModel.setContacts(contactList)

        saveContactsToPreferences(contactList)
        Log.d("ContactSync", "Synchronization complete")
    }

    private fun filterContacts(query: String) {
        val filteredContacts = allContacts.filter { it.name.contains(query, ignoreCase = true) }
        contactViewModel.setContacts(filteredContacts)
    }

    override fun onContactAdded(contact: Contact) {
        val currentContacts = contactViewModel.contacts.value.orEmpty().toMutableList()
        currentContacts.add(contact)
        saveContactsToPreferences(currentContacts)
        currentContacts.sortBy { it.name }
        allContacts = currentContacts
        contactViewModel.setContacts(currentContacts)
    }

    override fun onContactEdited(oldContact: Contact, newContact: Contact) {
        val currentContacts = contactViewModel.contacts.value.orEmpty().toMutableList()
        val index =
            currentContacts.indexOfFirst { it.name == oldContact.name && it.number == oldContact.number }
        if (index != -1) {
            currentContacts[index] = newContact
            saveContactsToPreferences(currentContacts)
            currentContacts.sortBy { it.name }
            allContacts = currentContacts
            contactViewModel.setContacts(currentContacts)
        }
    }

    private fun deleteContact(contact: Contact) {
        val currentContacts = contactViewModel.contacts.value.orEmpty().toMutableList()
        currentContacts.remove(contact)
        saveContactsToPreferences(currentContacts)
        allContacts = currentContacts
        contactViewModel.setContacts(currentContacts)
        Toast.makeText(requireContext(), "Contact deleted", Toast.LENGTH_SHORT).show()
    }

    private fun saveContactsToPreferences(contacts: List<Contact>) {
        val editor = sharedPreferences.edit()
        val savedContactsJson = Gson().toJson(contacts)
        editor.putString("saved_contacts", savedContactsJson)
        editor.apply()
    }

    private fun addHeaders(contacts: List<Contact>): List<Contact> {
        val sortedContacts = contacts.sortedBy { it.name }
        val result = mutableListOf<Contact>()
        var lastInitial = ' '

        for (contact in sortedContacts) {
            val initial = contact.name.first().uppercaseChar()
            if (initial != lastInitial) {
                result.add(Contact(initial.toString(), "", true))
                lastInitial = initial
            }
            result.add(contact)
        }

        return result
    }

    private fun editContact(contact: Contact) {
        val dialog = EditContactDialogFragment()
        dialog.setContact(contact)
        dialog.setOnContactEditedListener(this)
        dialog.show(parentFragmentManager, "EditContactDialogFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}