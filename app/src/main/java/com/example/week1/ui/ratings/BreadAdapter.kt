package com.example.week1.ui.ratings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week1.R
import com.example.week1.data.Bread

class BreadAdapter(
    private val context: Context,
    private val breadList: List<Bread>
) : RecyclerView.Adapter<BreadAdapter.BreadViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreadViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_bread, parent, false)
        return BreadViewHolder(view)
    }

    override fun onBindViewHolder(holder: BreadViewHolder, position: Int) {
        val bread = breadList[position]
        holder.breadName.text = bread.name
        Glide.with(context).load(bread.imagePath).into(holder.breadImage)
        holder.breadRating.text = bread.rating.toString()

        holder.breadRating.setOnClickListener {
            showRatingDialog(holder, bread)
        }
    }

    override fun getItemCount(): Int {
        return breadList.size
    }

    inner class BreadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val breadImage: ImageView = itemView.findViewById(R.id.bread_image)
        val breadName: TextView = itemView.findViewById(R.id.bread_name)
        val breadRating: TextView = itemView.findViewById(R.id.bread_rating)
    }

    private fun showRatingDialog(holder: BreadViewHolder, bread: Bread) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Rate ${bread.name}")

        val ratingBar = RatingBar(context).apply {
            max = 5
            stepSize = 1f
            rating = bread.rating
        }

        builder.setView(ratingBar)
        builder.setPositiveButton("OK") { _, _ ->
            val rating = ratingBar.rating
            bread.rating = rating
            holder.breadRating.text = rating.toString()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }
}