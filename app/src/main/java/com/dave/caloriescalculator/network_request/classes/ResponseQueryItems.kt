package com.dave.caloriescalculator.network_request.classes

import com.google.gson.annotations.SerializedName

/**
 * Indicates a data class that contains a format that we expect from an endpoint
 */
data class ResponseQueryItems (
    @SerializedName("common") val common: List<CommonData>,
    @SerializedName("branded") val branded: List<BrandedData>

    )

data class CommonData(
    @SerializedName("food_name") val food_name: String,
    @SerializedName("serving_qty") val serving_qty: String,

)
data class BrandedData(
    @SerializedName("food_name") val food_name: String,
    @SerializedName("serving_qty") val serving_qty: String,
    @SerializedName("nf_calories") val nf_calories: String,

)