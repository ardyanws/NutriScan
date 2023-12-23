package com.ardyan.capstonenutriscan.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts
import com.ardyan.capstonenutriscan.R
import com.ardyan.capstonenutriscan.databinding.ActivityLoginBinding
import com.ardyan.capstonenutriscan.setting.SettingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class LoginActivity : PrimaryActivity() {
    private var binding: ActivityLoginBinding? = null
    private lateinit var auth: FirebaseAuth

    private lateinit var googleLoginClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleLoginClient = GoogleSignIn.getClient(this, gso)


        binding?.tvRegister?.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        binding?.btnSignIn?.setOnClickListener{
            loginUser()
        }
        binding?.btnSignInWithGoogle?.setOnClickListener{loginWithGoogle()}
    }

    private fun loginUser() {
        val email = binding?.etSinInEmail?.text.toString()
        val password = binding?.etSinInPassword?.text.toString()

        if (validateForm(email, password)) {
            showProgressBar()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Ambil informasi tambahan pengguna termasuk nama tampilan
                        auth.currentUser?.reload()?.addOnCompleteListener { reloadTask ->
                            if (reloadTask.isSuccessful) {
                                val userName = auth.currentUser?.displayName

                                // Log untuk memeriksa nilai userName
                                Log.d("LoginActivity", "Reload successful, userName: $userName")

                                navigateToMainActivity(userName)
                            } else {
                                Log.e("LoginActivity", "Failed to reload user", reloadTask.exception)
                                showToast(this, "Gagal memperbarui informasi pengguna!")
                                hideProgressBar()
                            }
                        }
                    } else {
                        showToast(this, "Gagal login, coba lagi!")
                        hideProgressBar()
                    }
                }
        }
    }

    private fun navigateToMainActivity(userName: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userName", userName)
        startActivity(intent)
        finish()
        hideProgressBar()
    }

    private fun loginWithGoogle(){
        val loginIntent = googleLoginClient.signInIntent
        launcher.launch(loginIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account:GoogleSignInAccount? = task.result
            if(account!=null){
                updateUI(account)
            }
        }else{
            showToast(this, "Gagal Login, Coba lagi!")
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        showProgressBar()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("userName", account?.displayName)
                startActivity(intent)
                finish()
                hideProgressBar()
            } else {
                showToast(this, "Gagal login, coba lagi!")
                hideProgressBar()
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
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