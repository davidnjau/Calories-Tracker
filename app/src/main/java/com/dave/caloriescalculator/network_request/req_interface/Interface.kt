package com.dave.caloriescalculator.network_request.req_interface

import com.dave.caloriescalculator.network_request.classes.QueryBody
import com.dave.caloriescalculator.network_request.classes.ResponsePostItems
import com.dave.caloriescalculator.network_request.classes.ResponseQueryItems
import retrofit2.Call
import retrofit2.http.*


interface Interface {
    /**
     * Indicates all the endpoints to be consumed and their required parameters
     * and the type of response required
     */

    @POST("v2/natural/nutrients")
    fun postNutrients(@HeaderMap headers: Map<String, String>, @Body queryBody: QueryBody): Call<ResponsePostItems>

    @GET("/v2/search/instant?branded=true&common=true&detailed=false&self=false")
    fun getQueryItems(@HeaderMap headers: Map<String, String>, @Query("query") query: String)
    : Call<ResponseQueryItems>

    @GET("v2/search/item")
    fun getQueryQRCode(@HeaderMap headers: Map<String, String>, @Query("upc") id: String): Call<ResponsePostItems>

}