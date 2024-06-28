package com.example.week1.ui.breadfeed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.week1.databinding.FragmentBreadfeedBinding

/* BreadfeedFragment displays a grid of bread posts using RecyclerView.
 * It uses a ViewModel to manage the data.
 */
class BreadfeedFragment : Fragment() {

    private var _binding: FragmentBreadfeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BreadfeedAdapter
    private lateinit var viewModel: BreadfeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreadfeedBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(BreadfeedViewModel::class.java)
        Log.d("BreadfeedFragment", "onCreateView: ViewModel and Binding initialized")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BreadfeedAdapter(requireContext(), viewModel.breadPosts)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
        Log.d("BreadfeedFragment", "onViewCreated: RecyclerView and Adapter set")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("BreadfeedFragment", "onDestroyView: Binding released")
    }
}