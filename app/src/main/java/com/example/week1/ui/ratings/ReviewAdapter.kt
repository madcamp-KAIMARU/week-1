package com.example.week1.ui.ratings

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        val reviewPhoto: ImageView = view.findViewById(R.id.review_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.reviewContent.text = review.reviewContent
        holder.ratingBar.rating = review.myRating

        // 이미지 URI가 null이 아닌 경우에만 이미지를 설정합니다.
        if (!review.photoPath.isNullOrEmpty()) {
            val imageUri = Uri.parse(review.photoPath)
            holder.reviewPhoto.setImageURI(imageUri)
            holder.reviewPhoto.visibility = View.VISIBLE
        } else {
            holder.reviewPhoto.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = reviews.size
}