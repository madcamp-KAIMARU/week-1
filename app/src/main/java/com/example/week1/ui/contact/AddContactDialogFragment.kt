package com.example.week1.ui.contact

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.week1.R

class AddContactDialogFragment : DialogFragment() {

    interface OnContactAddedListener {
        fun onContactAdded(contact: Contact)
    }

    private var listener: OnContactAddedListener? = null

    private var name: String? = null
    private var number: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_contact, null)

        val nameEditText = view.findViewById<EditText>(R.id.editTextName)
        val numberEditText = view.findViewById<EditText>(R.id.editTextNumber)

        builder.setView(view)
            .setTitle("Add Contact")
            .setPositiveButton("Save") { dialog, id ->
                name = nameEditText.text.toString()
                number = numberEditText.text.toString()

                if (!name.isNullOrEmpty() && !number.isNullOrEmpty()) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_CONTACTS), 100)
                    } else {
                        addContact(name!!, number!!)
                        dialog.dismiss()  // 다이얼로그를 닫습니다.
                    }
                } else {
                    Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

        return builder.create()
    }

    private fun addContact(name: String, number: String) {
        val resolver = requireActivity().contentResolver

        val contentValues = ContentValues().apply {
            put(ContactsContract.RawContacts.ACCOUNT_TYPE, "")
            put(ContactsContract.RawContacts.ACCOUNT_NAME, "")
        }

        val uri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues)
        val rawContactId = uri?.lastPathSegment?.toLongOrNull() ?: return

        val dataValues = arrayOf(
            ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            },
            ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                put(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            }
        )

        for (dataValue in dataValues) {
            val resultUri = resolver.insert(ContactsContract.Data.CONTENT_URI, dataValue)
            if (resultUri != null) {
                // 삽입 성공
                listener?.onContactAdded(Contact(name, number))
                Toast.makeText(requireContext(), "Contact saved", Toast.LENGTH_SHORT).show()
                Log.d("AddContactDialogFragment", "Contact saved: $resultUri")
            } else {
                // 삽입 실패
                Toast.makeText(requireContext(), "Failed to save contact", Toast.LENGTH_SHORT).show()
                Log.d("AddContactDialogFragment", "Failed to save contact")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Permission granted, please save again", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun setOnContactAddedListener(listener: OnContactAddedListener) {
        this.listener = listener
    }
}
