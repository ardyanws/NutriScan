package com.ardyan.capstonenutriscan.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.ardyan.capstonenutriscan.R
import com.ardyan.capstonenutriscan.databinding.ActivityLoginBinding
import com.ardyan.capstonenutriscan.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : PrimaryActivity() {
    private var binding: ActivityRegisterBinding? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        binding?.tvLoginPage?.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding?.btnSignUp?.setOnClickListener{registerUser()}
    }

    // ...

    private fun registerUser() {
        val name = binding?.etSinUpName?.text.toString()
        val email = binding?.etSinUpEmail?.text.toString()
        val password = binding?.etSinUpPassword?.text.toString()

        if (validateForm(name, email, password)) {
            showProgressBar()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Set display name setelah pengguna berhasil dibuat
                        val user = Firebase.auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateProfileTask ->
                                if (updateProfileTask.isSuccessful) {
                                    Log.d("RegisterActivity", "Display name set successfully")
                                } else {
                                    Log.e("RegisterActivity", "Failed to set display name", updateProfileTask.exception)
                                }
                            }

                        showToast(this, "Berhasil membuat akun")
                        hideProgressBar()

                        // Navigasi ke MainActivity setelah berhasil membuat akun
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra("userName", name)
                        startActivity(intent)
                        finish()
                    } else {
                        showToast(this, "Gagal membuat akun, coba lagi!")
                        hideProgressBar()
                    }
                }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name)->{
                binding?.tilName?.error = "Enter name"
                false
            }
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()->{
                binding?.tilEmail?.error = "Enter valid email address"
                false
            }
            TextUtils.isEmpty(password)->{
                binding?.tilPassword?.error = "Enter password"
                false
            }
            else -> { true }
        }
    }
}