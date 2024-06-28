package com.example.week1.ui.breadfeed

import java.io.Serializable

/* BreadPost is a data class to represent a bread post.
 * It contains the image URL and description of the bread post.
 */
data class BreadPost(val imageUrl: String, val description: String) : Serializable