package com.example.week1.ui.ratings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R

class ReviewAdapter(
    private val context: Context,
    private val reviews: List<ReviewItem>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewContent: TextView = view.findViewById(R.id.review_content)
        val ratingBar: RatingBar = view.findViewById(R.id.review_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.reviewContent.text = review.reviewContent
        holder.ratingBar.rating = review.myRating
    }

    override fun getItemCount(): Int = reviews.size
}
