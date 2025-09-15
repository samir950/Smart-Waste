package com.qbrain.smartwaste.network

import com.qbrain.smartwaste.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>
    
    @GET("auth/profile")
    suspend fun getProfile(): Response<UserProfile>
    
    // Bins
    @GET("bins/nearby")
    suspend fun getNearbyBins(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Int = 500
    ): Response<NearbyBinsResponse>
    
    @GET("bins/{binId}/status")
    suspend fun getBinStatus(@Path("binId") binId: String): Response<BinStatus>
    
    @POST("bins/{binId}/collect")
    suspend fun collectBin(
        @Path("binId") binId: String,
        @Body collectRequest: CollectBinRequest
    ): Response<CollectBinResponse>
    
    @POST("bins/{binId}/maintenance")
    suspend fun reportMaintenance(
        @Path("binId") binId: String,
        @Body maintenanceRequest: MaintenanceRequest
    ): Response<MaintenanceResponse>
    
    @GET("bins/collection-priority")
    suspend fun getCollectionPriority(): Response<CollectionPriorityResponse>
    
    // Vehicles
    @GET("vehicles/{vehicleId}/route")
    suspend fun getVehicleRoute(@Path("vehicleId") vehicleId: String): Response<VehicleRouteResponse>
    
    @PUT("vehicles/{vehicleId}/status")
    suspend fun updateVehicleStatus(
        @Path("vehicleId") vehicleId: String,
        @Body statusUpdate: VehicleStatusUpdate
    ): Response<ApiResponse>
    
    // Routes
    @PUT("routes/{routeId}/progress")
    suspend fun updateRouteProgress(
        @Path("routeId") routeId: String,
        @Body progressUpdate: RouteProgressUpdate
    ): Response<RouteProgressResponse>
}