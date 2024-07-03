package com.example.week1.ui.ratings

data class ReviewItem(
    val breadName: String,
    val reviewContent: String,
    val myRating: Float,
    val photoPath: String? = null // 사진 경로 추가
)