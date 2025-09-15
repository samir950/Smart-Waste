package com.qbrain.smartwaste.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.qbrain.smartwaste.R
import com.qbrain.smartwaste.databinding.ActivityBinDetailsBinding
import com.qbrain.smartwaste.models.BinStatus
import com.qbrain.smartwaste.network.RetrofitClient
import com.qbrain.smartwaste.utils.Constants
import kotlinx.coroutines.launch

class BinDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var binding: ActivityBinDetailsBinding
    private lateinit var googleMap: GoogleMap
    private var binId: String? = null
    private var binStatus: BinStatus? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBinDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binId = intent.getStringExtra("binId")
        
        setupToolbar()
        setupMap()
        setupUI()
        
        binId?.let { loadBinDetails(it) }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Bin Details"
    }
    
    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    
    private fun setupUI() {
        binding.btnReportIssue.setOnClickListener {
            // Navigate to report issue activity
            Toast.makeText(this, "Report issue functionality", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnRefresh.setOnClickListener {
            binId?.let { loadBinDetails(it) }
        }
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        
        binStatus?.let { updateMapLocation(it) }
    }
    
    private fun loadBinDetails(binId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getBinStatus(binId)
                
                if (response.isSuccessful && response.body() != null) {
                    binStatus = response.body()!!
                    updateUI(binStatus!!)
                    
                    if (::googleMap.isInitialized) {
                        updateMapLocation(binStatus!!)
                    }
                } else {
                    Toast.makeText(this@BinDetailsActivity, "Failed to load bin details", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BinDetailsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateUI(binStatus: BinStatus) {
        binding.apply {
            tvBinId.text = binStatus.binId
            tvFillPercentage.text = "${binStatus.fillPercentage}%"
            tvWeight.text = "${binStatus.weight} kg"
            tvBatteryLevel.text = "${binStatus.batteryLevel}%"
            tvTemperature.text = "${binStatus.temperature ?: "N/A"}°C"
            tvLastCollection.text = binStatus.lastCollection ?: "Never"
            
            // Update progress indicators
            progressFill.progress = binStatus.fillPercentage
            progressBattery.progress = binStatus.batteryLevel
            
            // Update status chip
            when (binStatus.status) {
                Constants.BIN_STATUS_FULL -> {
                    chipStatus.text = "Full"
                    chipStatus.setChipBackgroundColorResource(R.color.error_red)
                }
                Constants.BIN_STATUS_NEEDS_COLLECTION -> {
                    chipStatus.text = "Needs Collection"
                    chipStatus.setChipBackgroundColorResource(R.color.warning_orange)
                }
                Constants.BIN_STATUS_NORMAL -> {
                    chipStatus.text = "Normal"
                    chipStatus.setChipBackgroundColorResource(R.color.success_green)
                }
                else -> {
                    chipStatus.text = "Empty"
                    chipStatus.setChipBackgroundColorResource(R.color.primary_blue)
                }
            }
        }
    }
    
    private fun updateMapLocation(binStatus: BinStatus) {
        val location = LatLng(binStatus.location.lat, binStatus.location.lng)
        
        googleMap.clear()
        googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("Bin ${binStatus.binId}")
                .snippet("Fill: ${binStatus.fillPercentage}%")
        )
        
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
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