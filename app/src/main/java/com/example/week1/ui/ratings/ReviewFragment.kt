package com.example.week1.ui.ratings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.week1.R
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
            Log.d("ReviewFragment", "Save button clicked: reviewContent=$reviewContent, newRating=$newRating")
            saveReview(reviewContent, newRating)
            hideKeyboard()
            Snackbar.make(view, "후기가 저장되었습니다.", Snackbar.LENGTH_SHORT).show()
            parentFragmentManager.setFragmentResult("review_updated", Bundle().apply {
                putString("breadName", breadName)
                putFloat("myRating", newRating)
            })
            parentFragmentManager.popBackStack()
        }

        // 터치 이벤트를 설정하여 EditText 외의 영역을 클릭하면 키보드가 닫히도록 함
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }

        return view
    }

    private fun saveReview(content: String, rating: Float) {
        Log.d("ReviewFragment", "saveReview called: content=$content, rating=$rating")

        // Load current reviews
        val gson = Gson()
        val json = sharedPreferences.getString("reviews_list", null)
        val type = object : TypeToken<MutableList<ReviewItem>>() {}.type
        val reviews: MutableList<ReviewItem> = if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }

        // Add new review
        reviews.add(ReviewItem(breadName, content, rating))

        // Save updated reviews list
        val editor = sharedPreferences.edit()
        val updatedJson = gson.toJson(reviews)
        editor.putString("reviews_list", updatedJson)
        editor.apply()

        Log.d("ReviewFragment", "Reviews list saved: $updatedJson")
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
