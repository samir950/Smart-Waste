package com.qbrain.smartwaste.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.qbrain.smartwaste.databinding.ActivityProfileBinding
import com.qbrain.smartwaste.network.RetrofitClient
import com.qbrain.smartwaste.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfileBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupUI()
        loadUserProfile()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"
    }
    
    private fun setupUI() {
        binding.btnEditProfile.setOnClickListener {
            toggleEditMode()
        }
        
        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }
        
        binding.btnLogout.setOnClickListener {
            logout()
        }
        
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // Save notification preference
            Toast.makeText(this, "Notifications ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getProfile()
                
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    updateUI(profile)
                } else {
                    // Load from local storage as fallback
                    loadLocalProfile()
                }
            } catch (e: Exception) {
                loadLocalProfile()
            }
        }
    }
    
    private fun loadLocalProfile() {
        binding.apply {
            tvUserName.text = SharedPrefsManager.getUserName() ?: "User"
            etName.setText(SharedPrefsManager.getUserName() ?: "")
            tvUserType.text = SharedPrefsManager.getUserType()?.capitalize() ?: "Citizen"
            
            // Mock data for demonstration
            etEmail.setText("user@example.com")
            etPhone.setText("+91-9876543210")
            
            when (SharedPrefsManager.getUserType()) {
                "driver" -> {
                    performanceSection.visibility = View.VISIBLE
                    tvCollectionsCompleted.text = "156"
                    tvEfficiencyRating.text = "92%"
                    tvTotalDistance.text = "2,450 km"
                }
                else -> {
                    performanceSection.visibility = View.GONE
                }
            }
        }
    }
    
    private fun updateUI(profile: com.qbrain.smartwaste.models.UserProfile) {
        binding.apply {
            tvUserName.text = profile.name
            etName.setText(profile.name)
            etEmail.setText(profile.email)
            etPhone.setText(profile.phone)
            tvUserType.text = profile.userType.capitalize()
            
            if (profile.userType == "driver" && profile.performance != null) {
                performanceSection.visibility = View.VISIBLE
                tvCollectionsCompleted.text = profile.performance.collectionsToday.toString()
                tvEfficiencyRating.text = "${profile.performance.efficiency}%"
                tvTotalDistance.text = "N/A" // Add to API response if needed
            } else {
                performanceSection.visibility = View.GONE
            }
        }
    }
    
    private fun toggleEditMode() {
        val isEditing = binding.etName.isEnabled
        
        binding.apply {
            etName.isEnabled = !isEditing
            etEmail.isEnabled = !isEditing
            etPhone.isEnabled = !isEditing
            
            if (isEditing) {
                btnEditProfile.text = "Edit Profile"
                btnSaveProfile.visibility = View.GONE
            } else {
                btnEditProfile.text = "Cancel"
                btnSaveProfile.visibility = View.VISIBLE
            }
        }
    }
    
    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveProfile.isEnabled = false
        
        lifecycleScope.launch {
            try {
                // In a real app, make API call to update profile
                // val response = RetrofitClient.apiService.updateProfile(updateData)
                
                // For now, just update local storage
                SharedPrefsManager.saveUserName(name)
                
                Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                toggleEditMode()
                
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnSaveProfile.isEnabled = true
            }
        }
    }
    
    private fun logout() {
        SharedPrefsManager.clearAll()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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