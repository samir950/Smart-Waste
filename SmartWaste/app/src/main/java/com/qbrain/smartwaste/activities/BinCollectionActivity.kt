package com.qbrain.smartwaste.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.qbrain.smartwaste.R
import com.qbrain.smartwaste.databinding.ActivityBinCollectionBinding
import com.qbrain.smartwaste.models.CollectBinRequest
import com.qbrain.smartwaste.network.RetrofitClient
import com.qbrain.smartwaste.utils.Constants
import com.qbrain.smartwaste.utils.SharedPrefsManager
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class BinCollectionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityBinCollectionBinding
    private var binId: String? = null
    private var beforePhotoBase64: String? = null
    private var afterPhotoBase64: String? = null
    private var isCapturingBefore = true
    
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { bitmap ->
                val base64 = bitmapToBase64(bitmap)
                if (isCapturingBefore) {
                    beforePhotoBase64 = base64
                    binding.ivBeforePhoto.setImageBitmap(bitmap)
                    binding.tvBeforePhotoStatus.text = "Photo captured"
                    binding.tvBeforePhotoStatus.setTextColor(ContextCompat.getColor(this, R.color.success_green))
                } else {
                    afterPhotoBase64 = base64
                    binding.ivAfterPhoto.setImageBitmap(bitmap)
                    binding.tvAfterPhotoStatus.text = "Photo captured"
                    binding.tvAfterPhotoStatus.setTextColor(ContextCompat.getColor(this, R.color.success_green))
                }
                updateSubmitButtonState()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBinCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binId = intent.getStringExtra("binId")
        
        setupToolbar()
        setupUI()
        loadBinInfo()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Collect Bin"
    }
    
    private fun setupUI() {
        binding.btnTakeBeforePhoto.setOnClickListener {
            isCapturingBefore = true
            checkCameraPermissionAndCapture()
        }
        
        binding.btnTakeAfterPhoto.setOnClickListener {
            isCapturingBefore = false
            checkCameraPermissionAndCapture()
        }
        
        binding.btnSubmit.setOnClickListener {
            if (validateInput()) {
                submitCollection()
            }
        }
        
        // Set current date and time
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        binding.tvCollectionTime.text = currentDateTime
        
        updateSubmitButtonState()
    }
    
    private fun loadBinInfo() {
        binId?.let {
            binding.tvBinId.text = it
            // In a real app, load bin details from API
        }
    }
    
    private fun checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                Constants.CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            capturePhoto()
        }
    }
    
    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    
    private fun validateInput(): Boolean {
        val weight = binding.etWeight.text.toString().trim()
        
        if (weight.isEmpty()) {
            binding.etWeight.error = "Weight is required"
            return false
        }
        
        val weightValue = weight.toDoubleOrNull()
        if (weightValue == null || weightValue <= 0) {
            binding.etWeight.error = "Please enter a valid weight"
            return false
        }
        
        if (beforePhotoBase64 == null) {
            Toast.makeText(this, "Please take a before photo", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (afterPhotoBase64 == null) {
            Toast.makeText(this, "Please take an after photo", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun updateSubmitButtonState() {
        val hasWeight = binding.etWeight.text.toString().trim().isNotEmpty()
        val hasBeforePhoto = beforePhotoBase64 != null
        val hasAfterPhoto = afterPhotoBase64 != null
        
        binding.btnSubmit.isEnabled = hasWeight && hasBeforePhoto && hasAfterPhoto
    }
    
    private fun submitCollection() {
        val weight = binding.etWeight.text.toString().trim().toDouble()
        val notes = binding.etNotes.text.toString().trim()
        
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val request = CollectBinRequest(
                    vehicleId = "VH001", // In production, get from user session
                    collectedWeight = weight,
                    collectionTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date()),
                    beforePhoto = beforePhotoBase64,
                    afterPhoto = afterPhotoBase64,
                    driverId = SharedPrefsManager.getUserId() ?: "DR001"
                )
                
                val response = RetrofitClient.apiService.collectBin(binId!!, request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        this@BinCollectionActivity,
                        "Bin collected successfully! Collection ID: ${response.body()?.collectionId}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@BinCollectionActivity,
                        response.body()?.message ?: "Failed to record collection",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BinCollectionActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == Constants.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhoto()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}