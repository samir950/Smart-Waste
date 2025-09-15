package com.qbrain.smartwaste.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefsManager {
    private lateinit var sharedPreferences: SharedPreferences
    
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveToken(token: String) {
        sharedPreferences.edit().putString(Constants.TOKEN_KEY, token).apply()
    }
    
    fun getToken(): String? {
        return sharedPreferences.getString(Constants.TOKEN_KEY, null)
    }
    
    fun saveUserType(userType: String) {
        sharedPreferences.edit().putString(Constants.USER_TYPE_KEY, userType).apply()
    }
    
    fun getUserType(): String? {
        return sharedPreferences.getString(Constants.USER_TYPE_KEY, null)
    }
    
    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(Constants.USER_ID_KEY, userId).apply()
    }
    
    fun getUserId(): String? {
        return sharedPreferences.getString(Constants.USER_ID_KEY, null)
    }
    
    fun saveUserName(userName: String) {
        sharedPreferences.edit().putString(Constants.USER_NAME_KEY, userName).apply()
    }
    
    fun getUserName(): String? {
        return sharedPreferences.getString(Constants.USER_NAME_KEY, null)
    }
    
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}