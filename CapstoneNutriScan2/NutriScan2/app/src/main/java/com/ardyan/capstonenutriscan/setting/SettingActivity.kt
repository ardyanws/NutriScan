package com.ardyan.capstonenutriscan.setting

import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.preferencesDataStore
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModelProvider
import com.ardyan.capstonenutriscan.activities.MainActivity
import com.ardyan.capstonenutriscan.databinding.ActivitySettingBinding

@Suppress("DEPRECATION")
class SettingActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private var _binding: ActivitySettingBinding? = null
    private val binding get() = _binding!!

    // Deklarasikan variabel userName di luar metode onCreate
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val switchTheme = binding.themeMode

        // Tambahkan listener untuk tombol back
        switchTheme.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPressed()
                true
            } else {
                false
            }
        }

        val pref = SetPreferences.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(this, SetViewModelFactory(pref))[SetViewModel::class.java]

        // Ambil nilai userName dari intent dan tetapkan ke variabel userName
        userName = intent.getStringExtra("userName")

        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }
        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }
    }

    override fun onBackPressed() {
        // Gunakan nilai userName di dalam metode ini
        Log.d("SettingActivity", "UserName in SettingActivity: $userName") // Tambahkan log di sini

        // Gunakan nilai userName di dalam metode ini
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userName", userName)
        startActivity(intent)
        finish()

        super.onBackPressed()
    }
}
