package com.example.week1.ui.ratings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R

class RatingsFragment : Fragment() {

    private lateinit var ratingAdapter: RatingAdapter
    private lateinit var originalRatings: MutableList<RatingItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ratings, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_ratings)
        val searchEditText: EditText = view.findViewById(R.id.search_edit_text)

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
}
