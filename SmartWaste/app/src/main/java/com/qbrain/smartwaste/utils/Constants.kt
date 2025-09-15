package com.qbrain.smartwaste.utils

object Constants {
    const val BASE_URL = "http://10.0.2.2:3000/api/"
    const val SHARED_PREF_NAME = "smart_waste_prefs"
    const val TOKEN_KEY = "auth_token"
    const val USER_TYPE_KEY = "user_type"
    const val USER_ID_KEY = "user_id"
    const val USER_NAME_KEY = "user_name"
    
    // Request codes
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    const val CAMERA_PERMISSION_REQUEST_CODE = 1002
    const val PICK_IMAGE_REQUEST = 1003
    
    // Map constants
    const val DEFAULT_ZOOM = 15f
    const val LOCATION_UPDATE_INTERVAL = 30000L // 30 seconds
    
    // User types
    const val USER_TYPE_DRIVER = "driver"
    const val USER_TYPE_CITIZEN = "citizen"
    
    // Bin status
    const val BIN_STATUS_EMPTY = "empty"
    const val BIN_STATUS_NORMAL = "normal"
    const val BIN_STATUS_NEEDS_COLLECTION = "needs_collection"
    const val BIN_STATUS_FULL = "full"
}