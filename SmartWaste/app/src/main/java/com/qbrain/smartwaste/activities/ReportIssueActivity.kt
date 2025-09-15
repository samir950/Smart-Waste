package com.qbrain.smartwaste.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.qbrain.smartwaste.R
import com.qbrain.smartwaste.databinding.ActivityReportIssueBinding
import com.qbrain.smartwaste.models.MaintenanceRequest
import com.qbrain.smartwaste.network.RetrofitClient
import com.qbrain.smartwaste.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class ReportIssueActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityReportIssueBinding
    private var binId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportIssueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binId = intent.getStringExtra("binId")
        
        setupToolbar()
        setupUI()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Report Issue"
    }
    
    private fun setupUI() {
        binding.btnSubmit.setOnClickListener {
            if (validateInput()) {
                submitReport()
            }
        }
        
        // Set bin ID if provided
        binId?.let {
            binding.etBinId.setText(it)
            binding.etBinId.isEnabled = false
        }
    }
    
    private fun validateInput(): Boolean {
        val binId = binding.etBinId.text.toString().trim()
        val issueType = binding.spinnerIssueType.selectedItem.toString()
        val description = binding.etDescription.text.toString().trim()
        
        if (binId.isEmpty()) {
            binding.etBinId.error = "Bin ID is required"
            return false
        }
        
        if (description.isEmpty()) {
            binding.etDescription.error = "Description is required"
            return false
        }
        
        if (description.length < 10) {
            binding.etDescription.error = "Please provide more details (minimum 10 characters)"
            return false
        }
        
        return true
    }
    
    private fun submitReport() {
        val binId = binding.etBinId.text.toString().trim()
        val issueType = binding.spinnerIssueType.selectedItem.toString().lowercase().replace(" ", "_")
        val description = binding.etDescription.text.toString().trim()
        val priority = when (binding.rgPriority.checkedRadioButtonId) {
            R.id.rbHigh -> "high"
            R.id.rbMedium -> "medium"
            R.id.rbLow -> "low"
            else -> "medium"
        }
        
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val request = MaintenanceRequest(
                    issueType = issueType,
                    description = description,
                    reportedBy = SharedPrefsManager.getUserId() ?: "citizen",
                    priority = priority
                )
                
                val response = RetrofitClient.apiService.reportMaintenance(binId, request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        this@ReportIssueActivity,
                        "Issue reported successfully. Ticket ID: ${response.body()?.ticketId}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@ReportIssueActivity,
                        response.body()?.message ?: "Failed to report issue",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReportIssueActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
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