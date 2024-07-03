package com.example.week1.ui.ratings

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
        Log.d("ReviewListFragment", "onCreateView called")
        val view = inflater.inflate(R.layout.fragment_review_list, container, false)
        sharedPreferences = requireContext().getSharedPreferences("ratings_prefs", Context.MODE_PRIVATE)

        breadName = arguments?.getString("breadName") ?: ""

        val tvBreadName: TextView = view.findViewById(R.id.tv_bread_name)
        tvBreadName.text = "🍞 $breadName"

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_reviews)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val reviews = loadReviews().filter { it.breadName == breadName && it.reviewContent.isNotEmpty() }.toMutableList()
        Log.d("ReviewListFragment", "Loaded reviews: $reviews")

        reviewAdapter = ReviewAdapter(requireContext(), reviews) { position ->
            deleteReview(position)
        }
        recyclerView.adapter = reviewAdapter

        val btnBack: ImageButton = view.findViewById(R.id.btn_list_back)
        Log.d("ReviewListFragment", "Back button initialized: $btnBack")
        btnBack.setOnClickListener {
            Log.d("ReviewListFragment", "Back button clicked")
            navigateToRatingsFragment()
        }

        val fabAddReview: ExtendedFloatingActionButton = view.findViewById(R.id.fab_add_review)
        fabAddReview.setOnClickListener {
            Log.d("ReviewListFragment", "FAB Add Review clicked")
            navigateToReviewFragment()
        }

        return view
    }

    private fun loadReviews(): List<ReviewItem> {
        Log.d("ReviewListFragment", "loadReviews called")
        val gson = Gson()
        val json = sharedPreferences.getString("reviews_list", null)
        val type = object : TypeToken<List<ReviewItem>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun saveReviews(reviews: List<ReviewItem>) {
        Log.d("ReviewListFragment", "saveReviews called")
        val gson = Gson()
        val editor = sharedPreferences.edit()
        val json = gson.toJson(reviews)
        editor.putString("reviews_list", json)
        editor.apply()
    }

    private fun deleteReview(position: Int) {
        Log.d("ReviewListFragment", "deleteReview called")
        val reviews = loadReviews().toMutableList()
        reviews.removeAt(position)
        saveReviews(reviews)
        reviewAdapter.updateReviews(reviews)
    }

    private fun navigateToReviewFragment() {
        Log.d("ReviewListFragment", "navigateToReviewFragment called")
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
        Log.d("ReviewListFragment", "navigateToRatingsFragment called")
        val ratingsFragment = RatingsFragment()
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
            replace(R.id.nav_host_fragment_activity_main, ratingsFragment)
            addToBackStack(null)
        }

        logBackStack()
    }

    private fun logBackStack() {
        Log.d("ReviewListFragment", "logBackStack called")
        val fragmentManager = parentFragmentManager
        val backStackCount = fragmentManager.backStackEntryCount
        Log.d("BackStack", "BackStack count: $backStackCount")
        for (i in 0 until backStackCount) {
            val backStackEntry = fragmentManager.getBackStackEntryAt(i)
            Log.d("BackStack", "Fragment: ${backStackEntry.name}")
        }
    }
}