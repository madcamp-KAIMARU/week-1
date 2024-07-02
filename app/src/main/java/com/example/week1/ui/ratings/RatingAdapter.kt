package com.example.week1.ui.ratings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.week1.R

class RatingAdapter(
    private val context: Context,
    private var items: MutableList<RatingItem>,
    private val onMyRatingClick: (RatingItem) -> Unit
) : RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    inner class RatingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val positionNumber: TextView = view.findViewById(R.id.position_number)
        val breadImage: ImageView = view.findViewById(R.id.bread_image)
        val breadName: TextView = view.findViewById(R.id.bread_name)
        val peopleRatingText: TextView = view.findViewById(R.id.people_rating_text)
        val peopleRating: RatingBar = view.findViewById(R.id.people_rating)
        val myRatingText: TextView = view.findViewById(R.id.my_rating_text)
        val myRating: RatingBar = view.findViewById(R.id.my_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_rating, parent, false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val item = items[position]
        holder.positionNumber.text = (position + 1).toString() // Position starts from 1
        Glide.with(context).load(item.breadImage).into(holder.breadImage)
        holder.breadName.text = item.breadName
        holder.peopleRatingText.text = item.peopleRating.toString()
        holder.peopleRating.rating = item.peopleRating
        if (item.myRating == 0f) {
            holder.myRatingText.text = "아직 평점 없음"
            holder.myRating.rating = 0f
        } else {
            holder.myRatingText.text = item.myRating.toString()
            holder.myRating.rating = item.myRating
        }

        holder.myRating.setOnClickListener {
            onMyRatingClick(item)
        }

        holder.myRatingText.setOnClickListener {
            onMyRatingClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateRating(item: RatingItem, newRating: Float) {
        val index = items.indexOf(item)
        if (index != -1) {
            items[index] = item.copy(myRating = newRating)
            notifyItemChanged(index)
        }
    }

    fun updateList(newItems: MutableList<RatingItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
