package com.example.week1.ui.contact

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.week1.R

class AddContactDialogFragment : DialogFragment() {

    interface OnContactAddedListener {
        fun onContactAdded(contact: Contact)
    }

    private var listener: OnContactAddedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_contact, null)

        val nameEditText = view.findViewById<EditText>(R.id.editTextName)
        val numberEditText = view.findViewById<EditText>(R.id.editTextNumber)

        builder.setView(view)
            .setTitle("Add Contact")
            .setPositiveButton("Save") { dialog, id ->
                val name = nameEditText.text.toString()
                val number = numberEditText.text.toString()

                if (!name.isNullOrEmpty() && !number.isNullOrEmpty()) {
                    listener?.onContactAdded(Contact(name, number))
                    Toast.makeText(requireContext(), "Contact saved", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

        return builder.create()
    }

    fun setOnContactAddedListener(listener: OnContactAddedListener) {
        this.listener = listener
    }
}
