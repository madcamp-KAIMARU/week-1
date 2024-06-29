package com.example.week1.ui.ratings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R
import com.example.week1.data.Bread

class RatingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var breadAdapter: BreadAdapter
    private lateinit var breadList: List<Bread>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ratings, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_ratings)

        // Initialize bread list
        breadList = loadBreads()

        // Set up RecyclerView
        breadAdapter = BreadAdapter(requireContext(), breadList)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = breadAdapter

        return view
    }

    private fun loadBreads(): List<Bread> {
        // Load breads from assets/sungsimdang
        val breadNames = arrayOf("bread1", "bread2", "bread3") // Add all bread names here
        return breadNames.map { name ->
            val imagePath = "file:///android_asset/sungsimdang/$name.png"
            Bread(name, imagePath, 0f)
        }
    }
}