package com.example.week1.ui.breadfeed

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week1.databinding.ItemBreadPostBinding

class BreadfeedAdapter(
    private val context: Context,
    private var breadPosts: List<BreadPost>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<BreadfeedAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemBreadPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(breadPost: BreadPost) {
            Glide.with(context)
                .load(breadPost.imageUrl)
                .centerCrop()
                .into(binding.imageView)
            binding.root.setOnClickListener {
                val dialog = BreadImageDialogFragment.newInstance(
                    breadPost.imageUrl, breadPost.description,
                    breadPost.date, breadPost.currentParticipants, breadPost.maxParticipants
                )
                dialog.show(fragmentManager, "BreadImageDialogFragment")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBreadPostBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(breadPosts[position])
    }

    override fun getItemCount(): Int = breadPosts.size

    fun updateData(newPosts: List<BreadPost>) {
        breadPosts = newPosts
        notifyDataSetChanged()
    }
}