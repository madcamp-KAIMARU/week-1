package com.example.week1.ui.ratings

import ReviewItem
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReviewListFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var breadName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review_list, container, false)
        sharedPreferences = requireContext().getSharedPreferences("ratings_prefs", Context.MODE_PRIVATE)

        // Retrieve breadName from arguments
        breadName = arguments?.getString("breadName") ?: ""

        // Set breadName to TextView
        val tvBreadName: TextView = view.findViewById(R.id.tv_bread_name)
        tvBreadName.text = "🍞 $breadName"

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_reviews)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val reviews = loadReviews().filter { it.breadName == breadName && it.reviewContent.isNotEmpty() }.toMutableList()

        // Log loaded reviews for debugging
        Log.d("ReviewList", "Loaded reviews: $reviews")

        reviewAdapter = ReviewAdapter(requireContext(), reviews) { review ->
            deleteReview(review)
        }
        recyclerView.adapter = reviewAdapter

        // Set up back button to navigate to RatingsFragment
        val btnBack: ImageButton = view.findViewById(R.id.btn_list_back)
        Log.d("ReviewList", "Back button initialized: $btnBack")
        btnBack.setOnClickListener {
            Log.d("ReviewList", "Back button clicked")
            navigateToRatingsFragment()
        }

        // Set up floating action button to navigate to ReviewFragment
        val fabAddReview: ExtendedFloatingActionButton = view.findViewById(R.id.fab_add_review)
        fabAddReview.setOnClickListener {
            navigateToReviewFragment()
        }

        return view
    }

    private fun loadReviews(): List<ReviewItem> {
        val gson = Gson()
        val json = sharedPreferences.getString("reviews_list", null)
        val type = object : TypeToken<List<ReviewItem>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun deleteReview(review: ReviewItem) {
        val reviews = loadReviews().toMutableList()
        reviews.remove(review)

        // Save updated reviews list
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val updatedJson = gson.toJson(reviews)
        editor.putString("reviews_list", updatedJson)
        editor.apply()

        reviewAdapter.removeReview(review)
    }

    private fun navigateToReviewFragment() {
        val reviewFragment = ReviewFragment().apply {
            arguments = Bundle().apply {
                putString("breadName", breadName)
            }
        }

        parentFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            replace(R.id.nav_host_fragment_activity_main, reviewFragment)
            addToBackStack(null)
        }
    }

    private fun navigateToRatingsFragment() {
        val ratingsFragment = RatingsFragment()
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
            replace(R.id.nav_host_fragment_activity_main, ratingsFragment)
            addToBackStack(null)
        }

        // Log the back stack entries
        logBackStack()
    }

    private fun logBackStack() {
        val fragmentManager = parentFragmentManager
        val backStackCount = fragmentManager.backStackEntryCount
        Log.d("BackStack", "BackStack count: $backStackCount")
        for (i in 0 until backStackCount) {
            val backStackEntry = fragmentManager.getBackStackEntryAt(i)
            Log.d("BackStack", "Fragment: ${backStackEntry.name}")
        }
    }
}
