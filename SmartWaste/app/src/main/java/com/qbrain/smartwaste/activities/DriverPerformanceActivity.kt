package com.qbrain.smartwaste.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.qbrain.smartwaste.databinding.ActivityDriverPerformanceBinding
import com.qbrain.smartwaste.utils.SharedPrefsManager

class DriverPerformanceActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDriverPerformanceBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        loadPerformanceData()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Performance"
    }
    
    private fun loadPerformanceData() {
        // Mock performance data - in production, load from API
        binding.apply {
            tvDriverName.text = SharedPrefsManager.getUserName()
            tvCollectionsToday.text = "12"
            tvCollectionsWeek.text = "78"
            tvCollectionsMonth.text = "324"
            
            tvEfficiencyScore.text = "92%"
            progressEfficiency.progress = 92
            
            tvOnTimeDeliveries.text = "95%"
            progressOnTime.progress = 95
            
            tvFuelEfficiency.text = "8.5 km/L"
            tvDistanceCovered.text = "245 km"
            tvAverageSpeed.text = "35 km/h"
            
            tvRating.text = "4.8"
            ratingBar.rating = 4.8f
            
            tvTotalRewards.text = "₹2,450"
            tvBonusEarned.text = "₹450"
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