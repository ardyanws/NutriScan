package com.ardyan.capstonenutriscan.data.response

import com.google.gson.annotations.SerializedName

data class FoodResponse(
    @field:SerializedName("Response")
    val response: List<FoodItem>
)


data class FoodItem(

    @field:SerializedName("harga")
    val harga: Int,

    @field:SerializedName("nama_makanan")
    val namaMakanan: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String
)
