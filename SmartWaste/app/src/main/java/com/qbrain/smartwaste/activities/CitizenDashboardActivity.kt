package com.qbrain.smartwaste.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.qbrain.smartwaste.R
import com.qbrain.smartwaste.adapters.NearbyBinsAdapter
import com.qbrain.smartwaste.databinding.ActivityCitizenDashboardBinding
import com.qbrain.smartwaste.models.NearbyBin
import com.qbrain.smartwaste.network.RetrofitClient
import com.qbrain.smartwaste.utils.Constants
import com.qbrain.smartwaste.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class CitizenDashboardActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var binding: ActivityCitizenDashboardBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var nearbyBinsAdapter: NearbyBinsAdapter
    private var currentLocation: Location? = null
    private var nearbyBins = mutableListOf<NearbyBin>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitizenDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupMap()
        setupUI()
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        checkLocationPermission()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Smart Waste - Citizen"
        binding.tvWelcome.text = "Welcome, ${SharedPrefsManager.getUserName()}"
    }
    
    private fun setupRecyclerView() {
        nearbyBinsAdapter = NearbyBinsAdapter(nearbyBins) { bin ->
            // Handle bin click - show details or navigate
            showBinDetails(bin)
        }
        binding.rvNearbyBins.apply {
            layoutManager = LinearLayoutManager(this@CitizenDashboardActivity)
            adapter = nearbyBinsAdapter
        }
    }
    
    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    
    private fun setupUI() {
        binding.fabReportIssue.setOnClickListener {
            startActivity(Intent(this, ReportIssueActivity::class.java))
        }
        
        binding.btnRefresh.setOnClickListener {
            getCurrentLocationAndLoadBins()
        }
        
        binding.cardStats.setOnClickListener {
            // Show user statistics or profile
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            getCurrentLocationAndLoadBins()
        }
    }
    
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocationAndLoadBins()
        }
    }
    
    private fun getCurrentLocationAndLoadBins() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            == PackageManager.PERMISSION_GRANTED) {
            
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    val latLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM))
                    loadNearbyBins(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun loadNearbyBins(lat: Double, lng: Double) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getNearbyBins(lat, lng, 1000)
                
                if (response.isSuccessful && response.body() != null) {
                    nearbyBins.clear()
                    nearbyBins.addAll(response.body()!!.nearbyBins)
                    nearbyBinsAdapter.notifyDataSetChanged()
                    
                    updateMapMarkers()
                    updateStats()
                } else {
                    Toast.makeText(this@CitizenDashboardActivity, "Failed to load nearby bins", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CitizenDashboardActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateMapMarkers() {
        googleMap.clear()
        
        nearbyBins.forEach { bin ->
            val latLng = LatLng(bin.location.lat, bin.location.lng)
            val markerColor = when (bin.status) {
                Constants.BIN_STATUS_FULL -> BitmapDescriptorFactory.HUE_RED
                Constants.BIN_STATUS_NEEDS_COLLECTION -> BitmapDescriptorFactory.HUE_ORANGE
                Constants.BIN_STATUS_NORMAL -> BitmapDescriptorFactory.HUE_GREEN
                else -> BitmapDescriptorFactory.HUE_BLUE
            }
            
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Bin ${bin.binId}")
                    .snippet("Fill: ${bin.fillPercentage}% | Distance: ${bin.distance}m")
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
            )
        }
    }
    
    private fun updateStats() {
        val totalBins = nearbyBins.size
        val fullBins = nearbyBins.count { it.status == Constants.BIN_STATUS_FULL }
        val nearestBin = nearbyBins.minByOrNull { it.distance }
        
        binding.tvTotalBins.text = totalBins.toString()
        binding.tvFullBins.text = fullBins.toString()
        binding.tvNearestDistance.text = "${nearestBin?.distance ?: 0}m"
    }
    
    private fun showBinDetails(bin: NearbyBin) {
        val intent = Intent(this, BinDetailsActivity::class.java)
        intent.putExtra("binId", bin.binId)
        startActivity(intent)
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_citizen_dashboard, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun logout() {
        SharedPrefsManager.clearAll()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (::googleMap.isInitialized) {
                    googleMap.isMyLocationEnabled = true
                }
                getCurrentLocationAndLoadBins()
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}