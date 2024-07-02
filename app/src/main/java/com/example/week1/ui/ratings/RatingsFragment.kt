package com.example.week1.ui.ratings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RatingsFragment : Fragment() {

    private lateinit var ratingAdapter: RatingAdapter
    private lateinit var originalRatings: MutableList<RatingItem>
    private lateinit var filteredRatings: MutableList<RatingItem>
    private lateinit var sortSpinner: Spinner
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ratings, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_ratings)
        val searchEditText: EditText = view.findViewById(R.id.search_edit_text)
        sortSpinner = view.findViewById(R.id.sort_spinner)
        sharedPreferences = requireContext().getSharedPreferences("ratings_prefs", Context.MODE_PRIVATE)

        originalRatings = loadRatings()
        filteredRatings = originalRatings.toMutableList()

        ratingAdapter = RatingAdapter(requireContext(), filteredRatings) { ratingItem ->
            if (hasReviews(ratingItem.breadName)) {
                navigateToReviewListFragment(ratingItem)
            } else {
                navigateToReviewFragment(ratingItem)
            }
        }

        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = ratingAdapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRatings(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val sortOptions = resources.getStringArray(R.array.sort_options)
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        sortSpinner.adapter = spinnerAdapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> sortByRatingHighToLow()
                    1 -> sortByRatingLowToHigh()
                    2 -> sortByName()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        setFragmentResultListener("review_updated") { _, bundle ->
            val breadName = bundle.getString("breadName")
            val myRating = bundle.getFloat("myRating")
            updateRating(breadName, myRating)
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

    private fun filterRatings(query: String) {
        filteredRatings = if (query.isEmpty()) {
            originalRatings.toMutableList()
        } else {
            originalRatings.filter { it.breadName.contains(query, ignoreCase = true) }.toMutableList()
        }
        sortRatings()
        ratingAdapter.updateList(filteredRatings)
    }

    private fun navigateToReviewFragment(ratingItem: RatingItem) {
        val reviewFragment = ReviewFragment().apply {
            arguments = Bundle().apply {
                putString("breadName", ratingItem.breadName)
                putFloat("myRating", ratingItem.myRating)
            }
        }

        parentFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            replace(R.id.nav_host_fragment_activity_main, reviewFragment)
            addToBackStack(null)
        }
    }

    private fun navigateToReviewListFragment(ratingItem: RatingItem) {
        val reviewListFragment = ReviewListFragment().apply {
            arguments = Bundle().apply {
                putString("breadName", ratingItem.breadName)
            }
        }

        parentFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            replace(R.id.nav_host_fragment_activity_main, reviewListFragment)
            addToBackStack(null)
        }
    }

    private fun updateRating(breadName: String?, newRating: Float) {
        if (breadName != null) {
            originalRatings.find { it.breadName == breadName }?.let {
                it.myRating = newRating
                saveRatings()  // Save the updated ratings
                ratingAdapter.updateList(filteredRatings) // update filtered list
            }
        }
    }

    private fun hasReviews(breadName: String): Boolean {
        val reviews = loadReviews()
        val hasReview = reviews.any { it.breadName == breadName && it.reviewContent.isNotEmpty() }
        Log.d("RatingsFragment", "hasReviews for $breadName: $hasReview, reviews: $reviews")
        return hasReview
    }

    private fun sortByRatingHighToLow() {
        filteredRatings.sortByDescending { it.peopleRating }
        ratingAdapter.updateList(filteredRatings)
    }

    private fun sortByRatingLowToHigh() {
        filteredRatings.sortBy { it.peopleRating }
        ratingAdapter.updateList(filteredRatings)
    }

    private fun sortByName() {
        filteredRatings.sortBy { it.breadName }
        ratingAdapter.updateList(filteredRatings)
    }

    private fun sortRatings() {
        when (sortSpinner.selectedItemPosition) {
            0 -> sortByRatingHighToLow()
            1 -> sortByRatingLowToHigh()
            2 -> sortByName()
        }
        ratingAdapter.updateList(filteredRatings) // 목록을 업데이트하여 위치를 갱신
    }

    private fun saveRatings() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(originalRatings)
        editor.putString("ratings_list", json)
        editor.apply()
    }

    private fun loadRatings(): MutableList<RatingItem> {
        val gson = Gson()
        val json = sharedPreferences.getString("ratings_list", null)
        val type = object : TypeToken<MutableList<RatingItem>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            RatingsDummyData.getRatings().toMutableList()
        }
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

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
