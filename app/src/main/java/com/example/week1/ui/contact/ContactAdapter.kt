package com.example.week1.ui.contact

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class ContactAdapter(
    private val context: Context,
    private val contacts: List<Contact>,
    private val deleteListener: (Contact) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (contacts[position].isHeader) TYPE_HEADER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(context).inflate(R.layout.list_item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            (holder as HeaderViewHolder).bind(contacts[position])
        } else {
            (holder as ItemViewHolder).bind(contacts[position])
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTextView: TextView = itemView.findViewById(R.id.headerTextView)

        fun bind(contact: Contact) {
            headerTextView.text = contact.initial.toString()
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.contactName)
        private val numberTextView: TextView = itemView.findViewById(R.id.contactNumber)
        private val imageView: ImageView = itemView.findViewById(R.id.contactImage)
        private val moreButton: ImageButton = itemView.findViewById(R.id.moreButton)

        fun bind(contact: Contact) {
            nameTextView.text = contact.name
            numberTextView.text = contact.number
            imageView.setImageResource(R.drawable.croissant)

            moreButton.setOnClickListener {
                showPopupMenu(it, contact)
            }
        }

        private fun showPopupMenu(view: View, contact: Contact) {
            val popup = androidx.appcompat.widget.PopupMenu(context, view)
            popup.inflate(R.menu.contact_options_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        deleteListener(contact)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getSectionName(position: Int): String {
        return contacts[position].initial.toString()
    }
}