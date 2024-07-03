package com.example.week1.ui.ratings

import ReviewItem
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R
import java.text.SimpleDateFormat
import java.util.*

class ReviewAdapter(
    private val context: Context,
    private var reviews: MutableList<ReviewItem>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewRating: RatingBar = view.findViewById(R.id.review_rating)
        val reviewContent: TextView = view.findViewById(R.id.review_content)
        val ratingBar: RatingBar = view.findViewById(R.id.review_rating)
        val reviewPhoto: ImageView = view.findViewById(R.id.review_photo)
        val reviewDate: TextView = view.findViewById(R.id.review_date) // 새로운 필드 추가
        val btnDeleteReview: ImageButton = view.findViewById(R.id.btn_delete_review) // 삭제 버튼 추가
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.reviewRating.rating = review.myRating
        holder.reviewContent.text = review.reviewContent
        holder.ratingBar.rating = review.myRating
        holder.reviewDate.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(review.timestamp))

        if (!review.photoPath.isNullOrEmpty()) {
            val imageUri = Uri.parse(review.photoPath)
            holder.reviewPhoto.setImageURI(imageUri)
            holder.reviewPhoto.visibility = View.VISIBLE
        } else {
            holder.reviewPhoto.visibility = View.GONE
        }

        holder.btnDeleteReview.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = reviews.size

    fun updateReviews(newReviews: List<ReviewItem>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }
}
