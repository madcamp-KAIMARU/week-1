package com.example.week1.ui.breadfeed

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week1.R

/* BreadfeedAdapter is an adapter class for the RecyclerView to display bread posts.
 * It binds the bread post data to the RecyclerView items and handles item click events.
 */
class BreadfeedAdapter(private val context: Context, private val breadPosts: List<BreadPost>) :
    RecyclerView.Adapter<BreadfeedAdapter.ViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bread_post, parent, false)
        Log.d("BreadfeedAdapter", "onCreateViewHolder: View created for position $viewType")
        return ViewHolder(view)
    }

    // Bind the data to the views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = breadPosts[position]
        Glide.with(context).load(post.imageUrl).into(holder.breadImage)
        holder.itemView.setOnClickListener {
            Log.d("BreadfeedAdapter", "onBindViewHolder: Item clicked at position $position")
            val intent = Intent(context, BreadDetailActivity::class.java)
            intent.putExtra("bread_post", post)
            context.startActivity(intent)
        }
    }

    // Return the size of the dataset
    override fun getItemCount(): Int {
        val itemCount = breadPosts.size
        Log.d("BreadfeedAdapter", "getItemCount: Item count is $itemCount")
        return itemCount
    }

    // Provide a reference to the views for each data item
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val breadImage: ImageView = itemView.findViewById(R.id.bread_image)
    }
}