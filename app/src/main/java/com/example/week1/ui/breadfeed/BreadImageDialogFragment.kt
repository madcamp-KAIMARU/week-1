package com.example.week1.ui.breadfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.week1.databinding.FragmentBreadImageDialogBinding

/* BreadImageDialogFragment shows an enlarged image and description in a popup. */
class BreadImageDialogFragment : DialogFragment() {

    private var _binding: FragmentBreadImageDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreadImageDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUrl = arguments?.getString("image_url")
        val description = arguments?.getString("description")

        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.imageView)
        }
        binding.textView.text = description

        // Dismiss the dialog when clicking anywhere on the view
        binding.root.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(imageUrl: String, description: String): BreadImageDialogFragment {
            val fragment = BreadImageDialogFragment()
            val args = Bundle()
            args.putString("image_url", imageUrl)
            args.putString("description", description)
            fragment.arguments = args
            return fragment
        }
    }
}