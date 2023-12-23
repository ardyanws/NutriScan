package com.ardyan.capstonenutriscan.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SetViewModelFactory(private val pref: SetPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetViewModel::class.java)) {
            return SetViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}