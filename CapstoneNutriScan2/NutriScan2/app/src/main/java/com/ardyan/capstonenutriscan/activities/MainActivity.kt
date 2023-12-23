package com.ardyan.capstonenutriscan.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ardyan.capstonenutriscan.R
import com.ardyan.capstonenutriscan.bottomFragment.Detect
import com.ardyan.capstonenutriscan.bottomFragment.Home
import com.ardyan.capstonenutriscan.bottomFragment.Profile
import com.ardyan.capstonenutriscan.databinding.ActivityMainBinding
import com.ardyan.capstonenutriscan.setting.SettingActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode_key", false)

        // Terapkan tema berdasarkan nilai dark mode
        if (isDarkModeEnabled) {
            setTheme(R.style.YourDarkTheme)
        } else {
            setTheme(R.style.Theme_YourLightTheme)
        }

        // Perbarui userName dari intent
        val userName = intent.getStringExtra("userName")

        binding?.topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    navigateToSettingActivity(userName)
                    finish()
                    true
                }
                R.id.menu2 -> {
                    if (auth.currentUser != null) {
                        auth.signOut()
                        startActivity(Intent(this, GetStartedActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> {
                    Log.d("MainActivity", "Unhandled menu item clicked: ${menuItem.itemId}")
                    false
                }
            }
        }

        val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        val settingIcon = if (isDarkMode) R.drawable.baseline_settings_white_24 else R.drawable.baseline_settings_24
        val exitIcon = if (isDarkMode) R.drawable.baseline_logout_white_24 else R.drawable.baseline_logout_24

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)

        val settingMenu = toolbar.menu.findItem(R.id.menu1)
        settingMenu.setIcon(settingIcon)

        val exitMenu = toolbar.menu.findItem(R.id.menu2)
        exitMenu.setIcon(exitIcon)

        replaceFragment(Home())
        binding?.bottomNavigationView?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.camera -> replaceFragment(Detect())
                R.id.profile -> replaceFragment(Profile())
                else -> {}
            }
            true
        }

        binding?.bottomNavigationView?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.camera -> replaceFragment(Detect())
                R.id.profile -> {
                    val profileFragment = Profile()
                    val bundle = Bundle()
                    bundle.putString("userName", userName)
                    profileFragment.arguments = bundle
                    replaceFragment(profileFragment)
                }
                else -> {}
            }
            true
        }
    }

    private fun navigateToSettingActivity(userName: String?) {
        val intent = Intent(this, SettingActivity::class.java)
        intent.putExtra("userName", userName)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null // Hindari memory leaks
    }
}
