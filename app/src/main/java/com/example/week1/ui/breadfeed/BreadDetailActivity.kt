package com.example.week1.ui.breadfeed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.week1.databinding.ActivityBreadDetailBinding

/* BreadDetailActivity displays a large view of the bread image and its description. */
class BreadDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreadDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBreadDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("image_url")
        val description = intent.getStringExtra("description")
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.imageView)
        }
        binding.textView.text = description

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }
}