package com.example.week1.ui.breadfeed

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.week1.R
import com.github.chrisbanes.photoview.PhotoView

/* BreadDetailActivity displays details of a bread post.
 * It shows an image and description of the bread post.
 */
class BreadDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bread_detail)

        // Get the bread post from the intent
        val breadPost = intent.getSerializableExtra("bread_post") as BreadPost?

        // Find the views and set the data
        val breadImageView: PhotoView = findViewById(R.id.bread_image_view)
        val descriptionTextView: TextView = findViewById(R.id.description_text_view)

        // Check if breadPost is null and handle it
        if (breadPost == null) {
            Log.e("BreadDetailActivity", "onCreate: BreadPost is null")
            descriptionTextView.text = "Error loading bread details."
            return
        }

        // Log the loading process
        Log.d("BreadDetailActivity", "onCreate: Loading image and description")

        // Load the image using Glide
        Glide.with(this).load(breadPost.imageUrl).into(breadImageView)
        // Set the description text
        descriptionTextView.text = breadPost.description

        Log.d("BreadDetailActivity", "onCreate: Image and description loaded successfully")
    }
}