package com.example.week1.ui.ratings

data class RatingItem(
    val breadName: String,
    val breadImage: String,
    val peopleRating: Float,
    var myRating: Float // 변경 가능하도록 var로 변경
)