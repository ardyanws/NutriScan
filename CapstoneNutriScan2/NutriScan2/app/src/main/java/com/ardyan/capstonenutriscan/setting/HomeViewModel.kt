package com.ardyan.capstonenutriscan.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardyan.capstonenutriscan.data.response.FoodItem
import com.ardyan.capstonenutriscan.data.retrofit.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _foodList = MutableLiveData<List<FoodItem>>()
    val foodList: LiveData<List<FoodItem>> get() = _foodList

    fun fetchFoodData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.apiService.getAllFoods().execute()
                if (response.isSuccessful) {
                    _foodList.postValue(response.body())
                } else {
                    // Handle error case
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exception
            }
        }
    }
}
