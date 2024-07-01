package com.example.week1.ui.contact

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.week1.R

class AddContactDialogFragment : DialogFragment() {

    interface OnContactAddedListener {
        fun onContactAdded(contact: Contact)
    }

    private var listener: OnContactAddedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_contact, null)

        val nameEditText = view.findViewById<EditText>(R.id.editTextName)
        val numberEditText = view.findViewById<EditText>(R.id.editTextNumber)
        val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
        val buttonSave = view.findViewById<Button>(R.id.buttonSave)

        val dialog = builder.setView(view).create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        buttonCancel.setOnClickListener {
            dialog.cancel()
        }

        buttonSave.setOnClickListener {
            val name = nameEditText.text.toString()
            val number = numberEditText.text.toString()

            if (name.isNotEmpty() && number.isNotEmpty()) {
                listener?.onContactAdded(Contact(name, number))
                Toast.makeText(requireContext(), "연락처가 저장되었습니다", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "이름과 전화번호를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        return dialog
    }

    fun setOnContactAddedListener(listener: OnContactAddedListener) {
        this.listener = listener
    }
}
