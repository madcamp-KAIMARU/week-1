package com.example.week1.ui.contact

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.week1.R

class ContactAdapter(private val context: Context, private val contacts: List<Contact>, private val deleteListener: (Contact) -> Unit) : BaseAdapter() {

    override fun getCount(): Int {
        return contacts.size
    }

    override fun getItem(position: Int): Any {
        return contacts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false)
            holder = ViewHolder()
            holder.nameTextView = view.findViewById(R.id.contactName)
            holder.numberTextView = view.findViewById(R.id.contactNumber)
            holder.imageView = view.findViewById(R.id.contactImage)
            holder.deleteButton = view.findViewById(R.id.deleteButton)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val contact = contacts[position]
        holder.nameTextView?.text = contact.name
        holder.numberTextView?.text = contact.number
        holder.imageView?.setImageResource(R.drawable.croissant)

        view.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${contact.number}")
            }
            context.startActivity(intent)
        }

        holder.deleteButton?.setOnClickListener {
            deleteListener(contact)
        }

        return view
    }

    private class ViewHolder {
        var nameTextView: TextView? = null
        var numberTextView: TextView? = null
        var imageView: ImageView? = null
        var deleteButton: ImageButton? = null
    }
}
