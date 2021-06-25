package com.dave.caloriescalculator.network_request.classes

import com.google.gson.annotations.SerializedName

data class ResponsePostItems(
    @SerializedName("foods") val foods: List<FoodDetails>,
)

data class FoodDetails(
    @SerializedName("food_name") val food_name: String,
    @SerializedName("serving_qty") val serving_qty: String,
    @SerializedName("nf_calories") val nf_calories: String,

)
