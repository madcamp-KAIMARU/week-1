package com.example.week1.ui.contact

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.week1.R

class EditContactDialogFragment : DialogFragment() {

    interface OnContactEditedListener {
        fun onContactEdited(oldContact: Contact, newContact: Contact)
    }

    private var listener: OnContactEditedListener? = null
    private var contact: Contact? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_modify_contact, null)

        val nameEditText = view.findViewById<EditText>(R.id.editTextName)
        val numberEditText = view.findViewById<EditText>(R.id.editTextNumber)
        val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
        val buttonSave = view.findViewById<Button>(R.id.buttonSave)

        val dialog = builder.setView(view).create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        contact?.let {
            nameEditText.setText(it.name)
            numberEditText.setText(it.number)
        }

        buttonCancel.setOnClickListener {
            dialog.cancel()
        }

        buttonSave.setOnClickListener {
            val name = nameEditText.text.toString()
            val number = numberEditText.text.toString()
            val initial = name.firstOrNull()?.uppercaseChar() ?: ' '

            if (name.isNotEmpty() && number.isNotEmpty()) {
                val newContact = Contact(name, number, false, initial)
                listener?.onContactEdited(contact!!, newContact)
                Toast.makeText(requireContext(), "연락처가 수정되었습니다", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "이름과 전화번호를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        return dialog
    }

    fun setOnContactEditedListener(listener: OnContactEditedListener) {
        this.listener = listener
    }

    fun setContact(contact: Contact) {
        this.contact = contact
    }
}