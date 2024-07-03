package com.example.week1.ui.ratings

import ReviewItem
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewAdapter(
    private val context: Context,
    private val reviews: MutableList<ReviewItem>,
    private val onReviewDeleted: (ReviewItem) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewRating: RatingBar = view.findViewById(R.id.review_rating)
        val reviewContent: TextView = view.findViewById(R.id.review_content)
        val reviewDate: TextView = view.findViewById(R.id.review_date)
        val btnDeleteReview: ImageButton = view.findViewById(R.id.btn_delete_review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.reviewRating.rating = review.myRating
        holder.reviewContent.text = review.reviewContent
        holder.reviewDate.text = formatDate(review.timestamp)

        holder.btnDeleteReview.setOnClickListener {
            onReviewDeleted(review)
        }
    }

    override fun getItemCount(): Int = reviews.size

    fun removeReview(review: ReviewItem) {
        val position = reviews.indexOf(review)
        if (position != -1) {
            reviews.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
