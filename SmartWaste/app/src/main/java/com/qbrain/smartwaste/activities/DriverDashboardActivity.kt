package com.qbrain.smartwaste.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.qbrain.smartwaste.R
import com.qbrain.smartwaste.adapters.RouteStopsAdapter
import com.qbrain.smartwaste.databinding.ActivityDriverDashboardBinding
import com.qbrain.smartwaste.models.VehicleStatusUpdate
import com.qbrain.smartwaste.network.RetrofitClient
import com.qbrain.smartwaste.utils.Constants
import com.qbrain.smartwaste.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class DriverDashboardActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var binding: ActivityDriverDashboardBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var routeStopsAdapter: RouteStopsAdapter
    
    private var currentLocation: Location? = null
    private var assignedRoute: String? = null
    private var routeStops = mutableListOf<String>()
    private val locationUpdateHandler = Handler(Looper.getMainLooper())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupMap()
        setupUI()
        setupLocationUpdates()
        
        checkLocationPermission()
        loadDriverRoute()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Smart Waste - Driver"
        binding.tvDriverName.text = "Driver: ${SharedPrefsManager.getUserName()}"
    }
    
    private fun setupRecyclerView() {
        routeStopsAdapter = RouteStopsAdapter(routeStops) { binId ->
            // Navigate to bin collection
            val intent = Intent(this, BinCollectionActivity::class.java)
            intent.putExtra("binId", binId)
            startActivity(intent)
        }
        binding.rvRouteStops.apply {
            layoutManager = LinearLayoutManager(this@DriverDashboardActivity)
            adapter = routeStopsAdapter
        }
    }
    
    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    
    private fun setupUI() {
        binding.btnStartRoute.setOnClickListener {
            startRoute()
        }
        
        binding.btnEndRoute.setOnClickListener {
            endRoute()
        }
        
        binding.cardPerformance.setOnClickListener {
            startActivity(Intent(this, DriverPerformanceActivity::class.java))
        }
    }
    
    private fun setupLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    currentLocation = location
                    updateLocationOnMap(location)
                    sendLocationUpdate(location)
                }
            }
        }
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            startLocationUpdates()
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
            startLocationUpdates()
        }
    }
    
    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            == PackageManager.PERMISSION_GRANTED) {
            
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                Constants.LOCATION_UPDATE_INTERVAL
            ).build()
            
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }
    
    private fun loadDriverRoute() {
        val vehicleId = "VH001" // In production, get from user profile
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getVehicleRoute(vehicleId)
                
                if (response.isSuccessful && response.body() != null) {
                    val routeResponse = response.body()!!
                    
                    if (routeResponse.routeId != null) {
                        assignedRoute = routeResponse.routeId
                        routeStops.clear()
                        routeResponse.assignedBins?.let { routeStops.addAll(it) }
                        routeStopsAdapter.notifyDataSetChanged()
                        
                        updateRouteInfo(routeResponse.estimatedTime, routeResponse.estimatedDistance)
                        binding.btnStartRoute.isEnabled = true
                    } else {
                        binding.tvRouteStatus.text = "No route assigned"
                        binding.btnStartRoute.isEnabled = false
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@DriverDashboardActivity, "Failed to load route: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateRouteInfo(estimatedTime: Int?, estimatedDistance: Double?) {
        binding.tvEstimatedTime.text = "${estimatedTime ?: 0} min"
        binding.tvEstimatedDistance.text = "${String.format("%.1f", estimatedDistance ?: 0.0)} km"
        binding.tvTotalStops.text = routeStops.size.toString()
    }
    
    private fun startRoute() {
        binding.tvRouteStatus.text = "Route Active"
        binding.btnStartRoute.isEnabled = false
        binding.btnEndRoute.isEnabled = true
        
        // Update vehicle status to collecting
        updateVehicleStatus("collecting")
        
        Toast.makeText(this, "Route started! Navigate to first bin.", Toast.LENGTH_SHORT).show()
    }
    
    private fun endRoute() {
        binding.tvRouteStatus.text = "Route Completed"
        binding.btnStartRoute.isEnabled = false
        binding.btnEndRoute.isEnabled = false
        
        // Update vehicle status to idle
        updateVehicleStatus("idle")
        
        Toast.makeText(this, "Route completed successfully!", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateLocationOnMap(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM))
        
        // Add route markers if available
        // This would be enhanced with actual route data
    }
    
    private fun sendLocationUpdate(location: Location) {
        val vehicleId = "VH001" // In production, get from user profile
        
        lifecycleScope.launch {
            try {
                val locationUpdate = com.qbrain.smartwaste.models.Location(
                    location.latitude,
                    location.longitude
                )
                
                val statusUpdate = VehicleStatusUpdate(
                    location = locationUpdate,
                    speed = if (location.hasSpeed()) location.speed.toDouble() else null,
                    heading = if (location.hasBearing()) location.bearing.toDouble() else null,
                    status = null,
                    fuelLevel = null,
                    currentWeight = null,
                    odometer = null
                )
                
                RetrofitClient.apiService.updateVehicleStatus(vehicleId, statusUpdate)
            } catch (e: Exception) {
                // Handle silently for location updates
            }
        }
    }
    
    private fun updateVehicleStatus(status: String) {
        val vehicleId = "VH001" // In production, get from user profile
        
        lifecycleScope.launch {
            try {
                val statusUpdate = VehicleStatusUpdate(
                    status = status,
                    location = null,
                    fuelLevel = null,
                    currentWeight = null,
                    odometer = null,
                    speed = null,
                    heading = null
                )
                
                RetrofitClient.apiService.updateVehicleStatus(vehicleId, statusUpdate)
            } catch (e: Exception) {
                Toast.makeText(this@DriverDashboardActivity, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_driver_dashboard, menu)
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
    
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}