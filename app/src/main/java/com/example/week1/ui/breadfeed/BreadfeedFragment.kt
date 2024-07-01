package com.example.week1.ui.breadfeed

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.week1.databinding.FragmentBreadfeedBinding
import com.example.week1.databinding.DialogAddBreadPostBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import com.example.week1.R

class BreadfeedFragment : Fragment() {

    private var _binding: FragmentBreadfeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BreadfeedAdapter
    private lateinit var viewModel: BreadfeedViewModel

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Log.d("BreadfeedFragment", "Image selected: $uri")
                showAddBreadPostDialog(it)
            } ?: run {
                Log.d("BreadfeedFragment", "Image selection failed")
            }
        }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                Log.d("BreadfeedFragment", "Photo taken")
                try {
                    val uri = Uri.parse(
                        MediaStore.Images.Media.insertImage(
                            requireContext().contentResolver,
                            bitmap,
                            "New Photo",
                            null
                        )
                    )
                    Log.d("BreadfeedFragment", "Photo URI: $uri")
                    showAddBreadPostDialog(uri)
                } catch (e: Exception) {
                    Log.e("BreadfeedFragment", "Failed to insert image: ${e.message}")
                }
            } ?: run {
                Log.d("BreadfeedFragment", "Photo taking failed")
            }
        }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var allGranted = true
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                Log.d("BreadfeedFragment", "$permissionName granted: $isGranted")
                if (!isGranted) {
                    allGranted = false
                }
            }

            if (allGranted) {
                proceedWithCamera()
            } else {
                showPermissionDeniedDialog()
            }
        }

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
        Log.d("BreadfeedFragment", "onViewCreated")

        adapter = BreadfeedAdapter(
            requireContext(),
            mutableListOf(),
            childFragmentManager
        ) // 수정: emptyList<BreadPost>()로 변경
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)  // 3 columns
        binding.recyclerView.adapter = adapter
        Log.d("BreadfeedFragment", "RecyclerView and Adapter set")

        viewModel.breadPosts.observe(viewLifecycleOwner, Observer { newPosts ->
            adapter.updateData(newPosts)
            binding.recyclerView.scrollToPosition(0)
            Log.d("BreadfeedFragment", "Bread posts updated: ${newPosts.size} items")
        })

        val uploadButton: FloatingActionButton = binding.uploadButton
        uploadButton.setOnClickListener {
            showUploadOptionsDialog()
        }
    }

    private fun showUploadOptionsDialog() {
        Log.d("BreadfeedFragment", "Showing upload options dialog")

        val dialogBinding = LayoutInflater.from(context).inflate(R.layout.dialog_upload_options, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background) // 다이얼로그 배경 설정

        dialogBinding.findViewById<Button>(R.id.buttonTakePhoto).setOnClickListener {
            dialog.dismiss()
            requestPermissionsForCamera()
        }

        dialogBinding.findViewById<Button>(R.id.buttonChooseFromGallery).setOnClickListener {
            dialog.dismiss()
            requestGalleryPermission()
        }

        dialog.show()
    }

    private fun requestPermissionsForCamera() {
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
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("BreadfeedFragment", "Gallery permission already granted")
                pickImage()
            }

            shouldShowRequestPermissionRationale(permission) -> {
                Log.d("BreadfeedFragment", "Showing gallery permission explanation dialog")
                showGalleryPermissionExplanationDialog()
            }

            else -> {
                Log.d("BreadfeedFragment", "Requesting gallery permission")
                requestPermissionsLauncher.launch(arrayOf(permission))
            }
        }
    }

    private fun showGalleryPermissionExplanationDialog() {
        Log.d("BreadfeedFragment", "Showing gallery permission explanation dialog")
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
        Log.d("BreadfeedFragment", "Showing permission denied dialog")
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
        Log.d("BreadfeedFragment", "Picking image from gallery")
        pickImageLauncher.launch("image/*")
    }

    private fun proceedWithCamera() {
        Log.d("BreadfeedFragment", "Opening camera")
        takePhotoLauncher.launch(null)
    }
    private fun showAddBreadPostDialog(imageUri: Uri) {
        Log.d("BreadfeedFragment", "Showing add bread post dialog with URI: $imageUri")
        val dialogBinding = DialogAddBreadPostBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // 다이얼로그 창의 배경을 설정
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        // 초기 상태에서 저장 버튼을 비활성화
        dialogBinding.buttonSave.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val description = dialogBinding.editTextDescription.text.toString().trim()
                val date = dialogBinding.editTextDate.text.toString().trim()
                val maxParticipants = dialogBinding.editTextMaxParticipants.text.toString().trim()
                val where2Meet = dialogBinding.editTextWhere2Meet.text.toString().trim()
                dialogBinding.buttonSave.isEnabled =
                    description.isNotEmpty() && date.isNotEmpty() && maxParticipants.isNotEmpty() && where2Meet.isNotEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        dialogBinding.editTextDescription.addTextChangedListener(textWatcher)
        dialogBinding.editTextDate.addTextChangedListener(textWatcher)
        dialogBinding.editTextMaxParticipants.addTextChangedListener(textWatcher)
        dialogBinding.editTextWhere2Meet.addTextChangedListener(textWatcher)

        dialogBinding.buttonSave.setOnClickListener {
            val description = dialogBinding.editTextDescription.text.toString()
            val date = dialogBinding.editTextDate.text.toString()
            val maxParticipants = dialogBinding.editTextMaxParticipants.text.toString().toInt()
            val where2Meet = dialogBinding.editTextWhere2Meet.text.toString()
            val newBreadPost = BreadPost(
                imageUri.toString(),
                description,
                date,
                1,
                maxParticipants,
                where2Meet,
                true,
            )
            viewModel.addBreadPost(newBreadPost)
            Log.d("BreadfeedFragment", "New bread post added: $newBreadPost")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDateTimePicker(editText: EditText) {
        Log.d("BreadfeedFragment", "Showing date time picker")
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                    editText.setText(
                        String.format(
                            "%04d-%02d-%02d %02d:%02d",
                            year,
                            month + 1,
                            dayOfMonth,
                            hourOfDay,
                            minute
                        )
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("BreadfeedFragment", "onDestroyView: Binding released")
    }
}