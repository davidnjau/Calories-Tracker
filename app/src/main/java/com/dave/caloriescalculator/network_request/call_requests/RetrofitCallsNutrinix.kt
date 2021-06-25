package com.dave.caloriescalculator.network_request.call_requests

import android.content.Context
import com.dave.caloriescalculator.R
import com.dave.caloriescalculator.network_request.builder.RetrofitBuilder
import com.dave.caloriescalculator.network_request.classes.*
import com.dave.caloriescalculator.network_request.req_interface.Interface
import com.uptech.easymeal.room_persitence.Entity.FoodDetailsinfo
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import java.util.HashMap


class RetrofitCallsNutrinix {


    fun fetchQuery(context: Context,query : String) = runBlocking{
        fetchByQuery(context, query)
    }
    private suspend fun fetchByQuery(context: Context, query : String)
    :Pair<List<CommonData>, List<BrandedData>> {
        /**
         * Fetch Nutritionix endpoint using a provided query,
         * populate the headers, and add the result to a list
         */

        var stringStringMap = HashMap<String, String>()
        stringStringMap["x-app-id"] = "9545d122"
        stringStringMap["x-app-key"] = "b5ff9d080007c16d594350fe3af61105"

        val job = Job()

        var commonList = ArrayList<CommonData>()
        var brandedList = ArrayList<BrandedData>()

        CoroutineScope(Dispatchers.IO + job).launch {

            val baseUrl = context.getString(R.string.base_url)

            val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
            val callSync: Call<ResponseQueryItems> =
                apiService.getQueryItems(stringStringMap, query)

            try {
                val response: Response<ResponseQueryItems> = callSync.execute()
                commonList = response.body()!!.common as ArrayList<CommonData>
                brandedList = response.body()!!.branded as ArrayList<BrandedData>

            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }.join()

        return Pair(commonList, brandedList)

    }

    fun fetchUsingQRCode(context: Context,query : String) = runBlocking{
        fetchByQRCode(context, query)
    }
    private suspend fun fetchByQRCode(context: Context, query : String):ArrayList<FoodDetailsinfo> {

        var stringStringMap = HashMap<String, String>()
        stringStringMap["x-app-id"] = "9545d122"
        stringStringMap["x-app-key"] = "b5ff9d080007c16d594350fe3af61105"

        val job = Job()

        var foodList = ArrayList<FoodDetailsinfo>()

        CoroutineScope(Dispatchers.IO + job).launch {

            val baseUrl = context.getString(R.string.base_url)

            val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
            val callSync: Call<ResponsePostItems> =
                apiService.getQueryQRCode(stringStringMap, query)

            try {
                val response: Response<ResponsePostItems> = callSync.execute()
                val foodDataList = response.body()!!.foods
                for (items in foodDataList){

                    val foodName = items.food_name
                    val calories = items.nf_calories
                    val servings = items.serving_qty

                    val foodDetailsinfo = FoodDetailsinfo(foodName, calories, servings, "")
                    foodList.add(foodDetailsinfo)
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }.join()



        return foodList

    }

    fun fetchCommonMealData(context: Context,query : String) = runBlocking{
        fetchCommonData(context, query)
    }
    private suspend fun fetchCommonData(context: Context, query: String): FoodDetails {

        var stringStringMap = HashMap<String, String>()
        stringStringMap["x-app-id"] = "9545d122"
        stringStringMap["x-app-key"] = "b5ff9d080007c16d594350fe3af61105"

        val job = Job()

        var foodName = ""
        var servingQnty = ""
        var calories = ""

        CoroutineScope(Dispatchers.IO + job).launch {

            val queryBody = QueryBody(query)

            val baseUrl = context.getString(R.string.base_url)

            val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
            val callSync: Call<ResponsePostItems> =
                apiService.postNutrients(stringStringMap, queryBody)

            try {
                val response: Response<ResponsePostItems> = callSync.execute()
                val foodList = response.body()!!.foods

                for (items in foodList) {

                    foodName = items.food_name
                    if (foodName == query) {

                        servingQnty = items.serving_qty
                        calories = items.nf_calories

                        break
                    }

                }


            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }.join()



        return FoodDetails(foodName, servingQnty, calories)

    }



}