package com.example.week1.ui.breadfeed

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week1.databinding.ItemBreadPostBinding

class BreadfeedAdapter(private val context: Context, private var breadPosts: List<BreadPost>) : RecyclerView.Adapter<BreadfeedAdapter.BreadfeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreadfeedViewHolder {
        val binding = ItemBreadPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BreadfeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BreadfeedViewHolder, position: Int) {
        val breadPost = breadPosts[position]
        Glide.with(context)
            .load(breadPost.imageUrl)
            .into(holder.binding.imageView)
        holder.binding.textView.text = breadPost.description

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BreadDetailActivity::class.java).apply {
                putExtra("image_url", breadPost.imageUrl)
                putExtra("description", breadPost.description)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return breadPosts.size
    }

    fun updateData(newBreadPosts: List<BreadPost>) {
        breadPosts = newBreadPosts
        notifyDataSetChanged()
    }

    inner class BreadfeedViewHolder(val binding: ItemBreadPostBinding) : RecyclerView.ViewHolder(binding.root)
}