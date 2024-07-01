package com.example.week1.ui.breadfeed

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.week1.databinding.FragmentBreadImageDialogBinding
import com.example.week1.R

class BreadImageDialogFragment : DialogFragment() {

    private var _binding: FragmentBreadImageDialogBinding? = null
    private val binding get() = _binding!!

    private var currentParticipants: Int = 0
    private var maxParticipants: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreadImageDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        val imageUrl = arguments?.getString("image_url")
        val description = arguments?.getString("description")
        val date = arguments?.getString("date")
        val where2Meet = arguments?.getString("where_to_meet")
        currentParticipants = arguments?.getInt("current_participants") ?: 0
        maxParticipants = arguments?.getInt("max_participants") ?: 0

        if (imageUrl != null) {
            Glide.with(context)
                .load(imageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("BreadImageDialogFragment", "Image load failed", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("BreadImageDialogFragment", "Image loaded successfully")
                        return false
                    }
                })
                .into(binding.imageView)
        }

        binding.textViewDescription.text = description
        binding.textViewDate.text = date
        binding.textViewParticipants.text = "$currentParticipants / $maxParticipants"
        binding.textViewWhere2Meet.text = where2Meet

        binding.buttonJoin.setOnClickListener {
            if (currentParticipants < maxParticipants) {
                currentParticipants++
                binding.textViewParticipants.text = "$currentParticipants / $maxParticipants"
                // Update the data source (e.g., ViewModel, database)
            } else {
                Toast.makeText(context, getString(R.string.event_full), Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonClose.setOnClickListener {
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
        fun newInstance(imageUrl: String, description: String, date: String, currentParticipants: Int, maxParticipants: Int, where2Meet: String): BreadImageDialogFragment {
            val fragment = BreadImageDialogFragment()
            val args = Bundle()
            args.putString("image_url", imageUrl)
            args.putString("description", description)
            args.putString("date", date)
            args.putInt("current_participants", currentParticipants)
            args.putInt("max_participants", maxParticipants)
            args.putString("where_to_meet", where2Meet)
            fragment.arguments = args
            return fragment
        }
    }
}