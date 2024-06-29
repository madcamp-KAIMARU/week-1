package com.example.week1.ui.ratings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.week1.databinding.FragmentRatingsBinding

class RatingsFragment : Fragment() {

    private var _binding: FragmentRatingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val ratingsViewModel =
            ViewModelProvider(this).get(RatingsViewModel::class.java)

        _binding = FragmentRatingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textRatings
        ratingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}