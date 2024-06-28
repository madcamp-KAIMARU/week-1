package com.example.week1.ui.breadfeed

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.week1.databinding.FragmentBreadfeedBinding
import com.example.week1.databinding.DialogAddBreadPostBinding

/* BreadfeedFragment displays a grid of bread posts using RecyclerView.
 * It uses a ViewModel to manage the data.
 */
class BreadfeedFragment : Fragment() {

    private var _binding: FragmentBreadfeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BreadfeedAdapter
    private lateinit var viewModel: BreadfeedViewModel

    // Register an activity result launcher for picking an image
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            showAddBreadPostDialog(it)
        }
    }

    // Register an activity result launcher for requesting permission
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            pickImage()
        } else {
            Log.d("BreadfeedFragment", "Permission denied")
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

        adapter = BreadfeedAdapter(requireContext(), emptyList())
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
        Log.d("BreadfeedFragment", "onViewCreated: RecyclerView and Adapter set")

        // Observe changes in the bread posts list
        viewModel.breadPosts.observe(viewLifecycleOwner, Observer { newPosts ->
            adapter.updateData(newPosts)
            binding.recyclerView.scrollToPosition(0)  // Scroll to the top to show the new item
        })

        // Upload button setup
        val uploadButton: FloatingActionButton = binding.uploadButton
        uploadButton.setOnClickListener {
            requestPermission()
        }
    }

    private fun requestPermission() {
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
                // Permission is granted, proceed to pick image
                pickImage()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // Show an explanation to the user
                showPermissionExplanationDialog()
            }
            else -> {
                // Request the permission
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("To upload images, this app requires access to your device's storage. Please grant the permission.")
            .setPositiveButton("OK") { _, _ ->
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
                requestPermissionLauncher.launch(permission)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("You have denied the permission to access storage. To upload images, please allow the permission from settings.")
            .setPositiveButton("Settings") { _, _ ->
                // Open app settings to grant permission
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }

    private fun showAddBreadPostDialog(imageUri: Uri) {
        val dialogBinding = DialogAddBreadPostBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Bread Post")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val description = dialogBinding.editTextDescription.text.toString()
                val newBreadPost = BreadPost(imageUri.toString(), description)
                viewModel.addBreadPost(newBreadPost)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("BreadfeedFragment", "onDestroyView: Binding released")
    }
}