package com.example.week1.ui.breadfeed

import android.graphics.drawable.Drawable
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

    private lateinit var breadPost: BreadPost
    private var listener: OnBreadPostUpdatedListener? = null

    interface OnBreadPostUpdatedListener {
        fun onBreadPostUpdated(updatedBreadPost: BreadPost)
    }

    fun setOnBreadPostUpdatedListener(listener: OnBreadPostUpdatedListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreadImageDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        breadPost = arguments?.getParcelable("bread_post") ?: return

        Glide.with(requireContext())
            .load(breadPost.imageUrl)
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

        binding.textViewDescription.text = breadPost.description
        binding.textViewDate.text = breadPost.date
        binding.textViewParticipants.text =
            "${breadPost.currentParticipants} / ${breadPost.maxParticipants}"
        binding.textViewWhere2Meet.text = breadPost.where2Meet

        // 버튼 텍스트 설정
        binding.buttonJoin.text =
            if (breadPost.hasJoined) getString(R.string.joined) else getString(R.string.join_now)

        binding.buttonJoin.setOnClickListener {
            if (breadPost.currentParticipants < breadPost.maxParticipants || breadPost.hasJoined) {
                breadPost.hasJoined = !breadPost.hasJoined
                if (breadPost.hasJoined) {
                    breadPost.currentParticipants++
                    binding.buttonJoin.text = getString(R.string.joined)
                } else {
                    breadPost.currentParticipants--
                    binding.buttonJoin.text = getString(R.string.join_now)
                }
                binding.textViewParticipants.text =
                    "${breadPost.currentParticipants} / ${breadPost.maxParticipants}"
            } else {
                Toast.makeText(context, getString(R.string.event_full), Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonClose.setOnClickListener {
            listener?.onBreadPostUpdated(breadPost)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(breadPost: BreadPost): BreadImageDialogFragment {
            val fragment = BreadImageDialogFragment()
            val args = Bundle()
            args.putParcelable("bread_post", breadPost)
            fragment.arguments = args
            return fragment
        }
    }
}