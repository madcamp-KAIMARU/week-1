package com.example.week1.ui.ratings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.week1.R
import com.google.android.material.snackbar.Snackbar

class ReviewFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var breadName: String
    private var myRating: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review, container, false)

        sharedPreferences = requireContext().getSharedPreferences("ratings_prefs", Context.MODE_PRIVATE)
        breadName = arguments?.getString("breadName") ?: ""
        myRating = arguments?.getFloat("myRating") ?: 0f

        val etReviewContent: EditText = view.findViewById(R.id.et_review_content)
        val btnBack: ImageButton = view.findViewById(R.id.btn_back)
        val btnSave: Button = view.findViewById(R.id.btn_save)
        val tvBreadName: TextView = view.findViewById(R.id.tv_bread_name)
        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)

        tvBreadName.text = "🍞 $breadName"
        ratingBar.rating = myRating

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSave.setOnClickListener {
            val reviewContent = etReviewContent.text.toString()
            val newRating = ratingBar.rating
            saveReview(reviewContent, newRating)
            Snackbar.make(view, "후기가 저장되었습니다.", Snackbar.LENGTH_SHORT).show()
            parentFragmentManager.setFragmentResult("review_updated", Bundle().apply {
                putString("breadName", breadName)
                putFloat("myRating", newRating)
            })
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun saveReview(content: String, rating: Float) {
        val editor = sharedPreferences.edit()
        editor.putString("review_$breadName", content)
        editor.putFloat("rating_$breadName", rating)
        editor.apply()
    }
}
