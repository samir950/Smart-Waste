package com.qbrain.smartwaste.models

import com.google.gson.annotations.SerializedName

// Authentication Models
data class LoginRequest(
    val email: String,
    val password: String,
    val userType: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val user: User?,
    val message: String?
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val userType: String,
    val location: Location?
)

data class RegisterResponse(
    val success: Boolean,
    val userId: String?,
    val message: String?
)

data class User(
    val userId: String,
    val name: String,
    val email: String,
    val userType: String,
    val permissions: List<String>
)

data class UserProfile(
    val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val userType: String,
    val location: Location?,
    val performance: Performance?
)

data class Performance(
    val collectionsToday: Int,
    val efficiency: Int,
    val rating: Double
)

// Location Models
data class Location(
    val lat: Double,
    val lng: Double,
    val address: String? = null
)

// Bin Models
data class NearbyBinsResponse(
    val nearbyBins: List<NearbyBin>
)

data class NearbyBin(
    val binId: String,
    val distance: Int,
    val fillPercentage: Int,
    val status: String,
    val location: Location,
    val type: String
)

data class BinStatus(
    val binId: String,
    val fillPercentage: Int,
    val weight: Double,
    val status: String,
    val lastCollection: String?,
    val batteryLevel: Int,
    val location: Location,
    val temperature: Double?
)

data class CollectBinRequest(
    val vehicleId: String,
    val collectedWeight: Double,
    val collectionTime: String,
    val beforePhoto: String?,
    val afterPhoto: String?,
    val driverId: String
)

data class CollectBinResponse(
    val success: Boolean,
    val collectionId: String?,
    val updatedStatus: String?,
    val message: String?
)

data class MaintenanceRequest(
    val issueType: String,
    val description: String,
    val reportedBy: String,
    val priority: String = "medium"
)

data class MaintenanceResponse(
    val success: Boolean,
    val ticketId: String?,
    val message: String?
)

data class CollectionPriorityResponse(
    val urgentBins: List<UrgentBin>,
    val totalBins: Int,
    val collectionNeeded: Int
)

data class UrgentBin(
    val binId: String,
    val fillPercentage: Int,
    val location: Location,
    val timeSinceFull: Int,
    val weight: Double,
    val status: String
)

// Vehicle Models
data class VehicleRouteResponse(
    val routeId: String?,
    val assignedBins: List<String>?,
    val estimatedTime: Int?,
    val estimatedDistance: Double?,
    val status: String?,
    val message: String?
)

data class VehicleStatusUpdate(
    val location: Location?,
    val fuelLevel: Int?,
    val currentWeight: Double?,
    val status: String?,
    val odometer: Double?,
    val speed: Double?,
    val heading: Double?
)

// Route Models
data class RouteProgressUpdate(
    val completedBinId: String,
    val completionTime: String,
    val collectedWeight: Double,
    val nextBinId: String?
)

data class RouteProgressResponse(
    val success: Boolean,
    val routeProgress: Int,
    val nextBinDetails: NextBinDetails?,
    val routeCompleted: Boolean,
    val message: String?
)

data class NextBinDetails(
    val binId: String,
    val location: Location,
    val fillPercentage: Int,
    val estimatedWeight: Int
)

// Generic Response
data class ApiResponse(
    val success: Boolean,
    val message: String?
)