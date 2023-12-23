package com.ardyan.capstonenutriscan.data.retrofit


import com.ardyan.capstonenutriscan.data.response.FoodItem
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("getAllFoods")
    fun getAllFoods(): Call<List<FoodItem>>
}

