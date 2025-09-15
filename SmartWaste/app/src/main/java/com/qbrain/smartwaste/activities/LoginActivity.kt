package com.qbrain.smartwaste.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.qbrain.smartwaste.R
import com.qbrain.smartwaste.databinding.ActivityLoginBinding
import com.qbrain.smartwaste.models.LoginRequest
import com.qbrain.smartwaste.network.RetrofitClient
import com.qbrain.smartwaste.utils.Constants
import com.qbrain.smartwaste.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            if (validateInput()) {
                performLogin()
            }
        }
        
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        binding.rgUserType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbDriver -> {
                    binding.tvUserTypeDescription.text = "Access route management and collection features"
                }
                R.id.rbCitizen -> {
                    binding.tvUserTypeDescription.text = "Find nearby bins and report issues"
                }
            }
        }
    }
    
    private fun validateInput(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            return false
        }
        
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }
        
        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            return false
        }
        
        return true
    }
    
    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val userType = when (binding.rgUserType.checkedRadioButtonId) {
            R.id.rbDriver -> Constants.USER_TYPE_DRIVER
            R.id.rbCitizen -> Constants.USER_TYPE_CITIZEN
            else -> Constants.USER_TYPE_CITIZEN
        }
        
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.login(
                    LoginRequest(email, password, userType)
                )
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()!!
                    
                    // Save user data
                    SharedPrefsManager.saveToken(loginResponse.token!!)
                    SharedPrefsManager.saveUserType(userType)
                    SharedPrefsManager.saveUserId(loginResponse.user!!.userId)
                    SharedPrefsManager.saveUserName(loginResponse.user.name)
                    
                    // Navigate to appropriate activity
                    val intent = when (userType) {
                        Constants.USER_TYPE_DRIVER -> Intent(this@LoginActivity, DriverDashboardActivity::class.java)
                        Constants.USER_TYPE_CITIZEN -> Intent(this@LoginActivity, CitizenDashboardActivity::class.java)
                        else -> Intent(this@LoginActivity, CitizenDashboardActivity::class.java)
                    }
                    
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        response.body()?.message ?: "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
            }
        }
    }
}