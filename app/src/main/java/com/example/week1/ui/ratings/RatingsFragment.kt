package com.example.week1.ui.ratings

import android.content.Context
import android.content.SharedPreferences
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
            showRatingDialog(ratingItem)
        }

        recyclerView.layoutManager = GridLayoutManager(context, 2) // 두 개의 열을 사용하여 Grid로 배치
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

        return view
    }

    private fun filterRatings(query: String) {
        filteredRatings = if (query.isEmpty()) {
            originalRatings.toMutableList()
        } else {
            originalRatings.filter { it.breadName.contains(query, ignoreCase = true) }.toMutableList()
        }
        sortRatings() // 현재 정렬 기준으로 필터링된 리스트를 정렬
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
                ratingItem.myRating = newRating
                ratingAdapter.updateRating(ratingItem, newRating)
                saveRatings()
                Toast.makeText(requireContext(), "${ratingItem.breadName}의 평점이 ${newRating}점으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("취소", null)
            .create()

        dialog.show()
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
}
