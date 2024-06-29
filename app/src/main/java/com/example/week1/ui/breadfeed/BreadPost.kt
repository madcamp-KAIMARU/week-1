package com.example.week1.ui.breadfeed

data class BreadPost(
    val imageUrl: String,
    val description: String,
    val date: String,
    var currentParticipants: Int,
    val maxParticipants: Int,
    val where2Meet: String
)