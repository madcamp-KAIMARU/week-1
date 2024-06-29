package com.example.week1.ui.contact

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.week1.databinding.FragmentContactBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ContactFragment : Fragment(), AddContactDialogFragment.OnContactAddedListener {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactViewModel: ContactViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        val root: View = binding.root

        contactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)
        sharedPreferences = requireContext().getSharedPreferences("contacts", Context.MODE_PRIVATE)

        val listView = binding.contactListView
        contactViewModel.contacts.observe(viewLifecycleOwner, Observer { contacts ->
            contacts?.let {
                val adapter = ContactAdapter(requireContext(), it) { contact ->
                    deleteContact(contact)
                }
                listView.adapter = adapter
            }
        })

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), 1)
        } else {
            loadContacts()
        }

        binding.fab.setOnClickListener {
            val dialog = AddContactDialogFragment()
            dialog.setOnContactAddedListener(this)
            dialog.show(parentFragmentManager, "AddContactDialogFragment")
        }

        return root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("test", "permission granted")
            loadContacts()
        } else {
            Log.d("test", "permission denied")
        }
    }

    private fun loadContacts() {
        val contactList = mutableListOf<Contact>()

        // Load contacts from phone
        val resolver = requireContext().contentResolver
        val cursor: Cursor? = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val number = cursor.getString(numberIndex)
                contactList.add(Contact(name, number))
            }
        }

        // Load contacts from SharedPreferences
        val savedContactsJson = sharedPreferences.getString("saved_contacts", "")
        if (!savedContactsJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<Contact>>() {}.type
            val savedContacts: List<Contact> = Gson().fromJson(savedContactsJson, type)
            contactList.addAll(savedContacts)
        }

        contactViewModel.setContacts(contactList)
    }

    override fun onContactAdded(contact: Contact) {
        // 연락처가 추가된 후 SharedPreferences에 저장하고 리스트를 갱신합니다.
        val currentContacts = contactViewModel.contacts.value.orEmpty().toMutableList()
        currentContacts.add(contact)

        // Save to SharedPreferences
        saveContactsToPreferences(currentContacts)

        contactViewModel.setContacts(currentContacts)
    }

    private fun deleteContact(contact: Contact) {
        val currentContacts = contactViewModel.contacts.value.orEmpty().toMutableList()
        currentContacts.remove(contact)

        // Save to SharedPreferences
        saveContactsToPreferences(currentContacts)

        contactViewModel.setContacts(currentContacts)
        Toast.makeText(requireContext(), "Contact deleted", Toast.LENGTH_SHORT).show()
    }

    private fun saveContactsToPreferences(contacts: List<Contact>) {
        val editor = sharedPreferences.edit()
        val savedContactsJson = Gson().toJson(contacts.filter { it !in contactViewModel.contacts.value.orEmpty() })
        editor.putString("saved_contacts", savedContactsJson)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
