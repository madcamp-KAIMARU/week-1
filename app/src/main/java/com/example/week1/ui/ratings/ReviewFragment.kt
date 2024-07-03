// LGTM
package com.example.week1.ui.ratings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.week1.R
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ReviewFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var breadName: String
    private var myRating: Float = 0f
    private var currentPhotoPath: String? = null

    companion object {
        private const val TAG = "ReviewFragment"
        private const val PREFS_NAME = "ratings_prefs"
        private const val REVIEWS_KEY = "reviews_list"
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_GALLERY_IMAGE = 2
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Log.d(TAG, "Image selected: $uri")
                view?.findViewById<ImageView>(R.id.review_photo)?.apply {
                    setImageURI(uri)
                    visibility = View.VISIBLE
                }
                currentPhotoPath = uri.toString()
            } ?: run {
                Log.d(TAG, "Image selection failed")
            }
        }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                Log.d(TAG, "Photo taken")
                try {
                    val uri = Uri.parse(
                        MediaStore.Images.Media.insertImage(
                            requireContext().contentResolver,
                            bitmap,
                            "New Photo",
                            null
                        )
                    )
                    Log.d(TAG, "Photo URI: $uri")
                    view?.findViewById<ImageView>(R.id.review_photo)?.apply {
                        setImageURI(uri)
                        visibility = View.VISIBLE
                    }
                    currentPhotoPath = uri.toString()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to insert image: ${e.message}")
                }
            } ?: run {
                Log.d(TAG, "Photo taking failed")
            }
        }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var allGranted = true
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                Log.d(TAG, "$permissionName granted: $isGranted")
                if (!isGranted) {
                    allGranted = false
                }
            }

            if (allGranted) {
                when {
                    permissions.containsKey(Manifest.permission.CAMERA) -> proceedWithCamera()
                    permissions.containsKey(Manifest.permission.READ_MEDIA_IMAGES) || permissions.containsKey(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) -> pickImage()
                }
            } else {
                showPermissionDeniedDialog()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: called")
        val view = inflater.inflate(R.layout.fragment_review, container, false)

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        breadName = arguments?.getString("breadName") ?: ""
        myRating = arguments?.getFloat("myRating") ?: 0f

        val etReviewContent: EditText = view.findViewById(R.id.et_review_content)
        val btnBack: ImageButton = view.findViewById(R.id.btn_back)
        val btnSave: Button = view.findViewById(R.id.btn_save)
        val tvBreadName: TextView = view.findViewById(R.id.tv_bread_name)
        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        val reviewPhoto: ImageView = view.findViewById(R.id.review_photo)
        val addPhotoButton: Button = view.findViewById(R.id.add_photo_button)

        tvBreadName.text = "🍞 $breadName"
        ratingBar.rating = myRating

        btnBack.setOnClickListener {
            Log.d(TAG, "btnBack: clicked")
            parentFragmentManager.popBackStack()
        }

        btnSave.setOnClickListener {
            val reviewContent = etReviewContent.text.toString()
            val newRating = ratingBar.rating
            Log.d(TAG, "btnSave: clicked - reviewContent=$reviewContent, newRating=$newRating")
            saveReview(reviewContent, newRating)
            hideKeyboard()
            Snackbar.make(view, "후기가 저장되었습니다.", Snackbar.LENGTH_SHORT).show()
            parentFragmentManager.setFragmentResult("review_updated", Bundle().apply {
                putString("breadName", breadName)
                putFloat("myRating", newRating)
            })
            parentFragmentManager.popBackStack()
        }

        addPhotoButton.setOnClickListener {
            Log.d(TAG, "addPhotoButton: clicked")
            showUploadOptionsDialog()
        }

        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }

        return view
    }

    private fun showUploadOptionsDialog() {
        Log.d(TAG, "showUploadOptionsDialog: called")
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_upload_options, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.buttonTakePhoto).setOnClickListener {
            Log.d(TAG, "showUploadOptionsDialog: buttonTakePhoto clicked")
            requestPermissionsForCamera()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.buttonChooseFromGallery).setOnClickListener {
            Log.d(TAG, "showUploadOptionsDialog: buttonChooseFromGallery clicked")
            requestGalleryPermission()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun requestPermissionsForCamera() {
        Log.d(TAG, "requestPermissionsForCamera: called")
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            proceedWithCamera()
        }
    }

    private fun requestGalleryPermission() {
        Log.d(TAG, "requestGalleryPermission: called")
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val permissionsToRequest = arrayOf(permission).filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            pickImage()
        }
    }

    private fun showGalleryPermissionExplanationDialog() {
        Log.d(TAG, "showGalleryPermissionExplanationDialog: called")
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("To upload images, this app requires access to your device's storage. Please grant the permission.")
            .setPositiveButton("OK") { _, _ ->
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
                requestPermissionsLauncher.launch(arrayOf(permission))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        Log.d(TAG, "showPermissionDeniedDialog: called")
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("You have denied the permission to access storage. To upload images, please allow the permission from settings.")
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
        .show()
    }

    private fun pickImage() {
        Log.d(TAG, "pickImage: called")
        pickImageLauncher.launch("image/*")
    }

    private fun proceedWithCamera() {
        Log.d(TAG, "proceedWithCamera: called")
        takePhotoLauncher.launch(null)
    }

    private fun saveReview(content: String, rating: Float) {
        Log.d(
            TAG,
            "saveReview: called - content=$content, rating=$rating, photoPath=$currentPhotoPath"
        )

        val gson = Gson()
        val json = sharedPreferences.getString(REVIEWS_KEY, null)
        val type = object : TypeToken<MutableList<ReviewItem>>() {}.type
        val reviews: MutableList<ReviewItem> = if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }

        val timestamp = System.currentTimeMillis() // 현재 시간으로 타임스탬프 설정

        reviews.add(ReviewItem(breadName, content, rating, currentPhotoPath, timestamp))

        val editor = sharedPreferences.edit()
        val updatedJson = gson.toJson(reviews)
        editor.putString(REVIEWS_KEY, updatedJson)
        editor.apply()

        Log.d(TAG, "saveReview: Reviews list saved - $updatedJson")
    }

    private fun hideKeyboard() {
        Log.d(TAG, "hideKeyboard: called")
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}