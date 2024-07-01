package com.example.week1.ui.ratings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R

class RatingsFragment : Fragment() {

    private lateinit var ratingAdapter: RatingAdapter
    private lateinit var originalRatings: MutableList<RatingItem>
    private lateinit var sortSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ratings, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_ratings)
        val searchEditText: EditText = view.findViewById(R.id.search_edit_text)
        sortSpinner = view.findViewById(R.id.sort_spinner)

        originalRatings = RatingsDummyData.getRatings().toMutableList()
        ratingAdapter = RatingAdapter(requireContext(), originalRatings.toMutableList()) { ratingItem ->
            showRatingDialog(ratingItem)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ratingAdapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRatings(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Initialize the Spinner for sorting
        val sortOptions = resources.getStringArray(R.array.sort_options)
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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

        return view
    }

    private fun filterRatings(query: String) {
        val filteredRatings = if (query.isEmpty()) {
            originalRatings
        } else {
            originalRatings.filter { it.breadName.contains(query, ignoreCase = true) }.toMutableList()
        }
        ratingAdapter.updateList(filteredRatings)
    }

    private fun showRatingDialog(ratingItem: RatingItem) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_rating, null)
        val ratingBar: RatingBar = dialogView.findViewById(R.id.dialog_rating_bar)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("내 평점 매기기")
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                val newRating = ratingBar.rating
                ratingAdapter.updateRating(ratingItem, newRating)
                Toast.makeText(requireContext(), "${ratingItem.breadName}의 평점이 ${newRating}점으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("취소", null)
            .create()

        dialog.show()
    }

    private fun sortByRatingHighToLow() {
        originalRatings.sortByDescending { it.peopleRating }
        ratingAdapter.updateList(originalRatings)
    }

    private fun sortByRatingLowToHigh() {
        originalRatings.sortBy { it.peopleRating }
        ratingAdapter.updateList(originalRatings)
    }

    private fun sortByName() {
        originalRatings.sortBy { it.breadName }
        ratingAdapter.updateList(originalRatings)
    }
}