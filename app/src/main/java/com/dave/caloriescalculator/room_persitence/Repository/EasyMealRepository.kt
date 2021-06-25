package com.uptech.easymeal.room_persitence.Repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.centafrique.foodsurvey.RoomPersitence.EasyMealDao
import com.dave.caloriescalculator.R
import com.dave.caloriescalculator.helperClass.MyFoods
import com.dave.caloriescalculator.room_persitence.classes.MealData
import com.uptech.easymeal.room_persitence.Entity.FoodDetailsinfo
import com.uptech.easymeal.room_persitence.Entity.Mealinfo
import kotlinx.coroutines.*

class EasyMealRepository(private val easyMealDao: EasyMealDao) {


    val urlsList: LiveData<List<FoodDetailsinfo>> = easyMealDao.getFoodLiveUrls()


    suspend fun addMeal(mealinfo: Mealinfo, context: Context){
        val job = Job()

        CoroutineScope(Dispatchers.IO + job).launch {

            val sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.app_name),Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor =  sharedPreferences.edit()

            val id = easyMealDao.insertMeal(mealinfo)
            editor.putString("mealId",id.toString())
            editor.apply()


        }.join()
    }
    suspend fun insertFoodMeal(foodDetailsinfo: FoodDetailsinfo){
        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {
            easyMealDao.insertFoodMeal(foodDetailsinfo)
        }.join()
    }

    suspend fun getMeals() : List<MealData>{

        val mealDataList = ArrayList<MealData>()

        val mealinfoList =  easyMealDao.getMeals()
        for (items in mealinfoList){

            val mealName = items.meal_name
            val mealId = items.id
            val mealDate = items.date

            val foodList = easyMealDao.getMealsFoodById(mealId.toString())
            var caloriesNo = 0.0
            var servingsNo = 0.0
            var foodItemsNumber = foodList.size

            for (foods in foodList){

                val calories = foods.calories.toDouble()
                val servings = foods.servings.toDouble()

                caloriesNo += calories
                servingsNo += servings

            }



            val mealData = MealData(mealId.toString(),getNewDate(mealDate), mealName,
                getNewCharacter(caloriesNo.toString()),
                getNewCharacter(servingsNo.toString()),
                foodItemsNumber.toString())
            mealDataList.add(mealData)
        }

        return mealDataList

    }

    private fun getNewDate(input: String):String{

        val s1 = input.substring(input.indexOf("/") + 1)
        val s2 = s1.substring(s1.indexOf("/") + 1)

        return s2

    }
    private fun getNewCharacter(input: String):String{
        return input.split(".")[0]
    }
    suspend fun deleteMeal(mealId: Int){

        val foodList = easyMealDao.getMealsFoodById(mealId.toString())
        for (items in foodList){

            val id = items.id
            if (id != null) {
                deleteFoodItem(id)
            }

        }
        easyMealDao.deleteMeal(mealId)

    }
    suspend fun deleteFoodItem(foodId: Int){

        return easyMealDao.deleteFoodItem(foodId)

    }

    suspend fun getMealsById(mealId : String) : Mealinfo{

        return easyMealDao.getMealsById(mealId)

    }
    suspend fun getFoodById(foodId : Int) : FoodDetailsinfo{

        return easyMealDao.getFoodById(foodId)

    }
    suspend fun getFoodMealsById(mealId : String) : List<FoodDetailsinfo>{

        val mealDataList = ArrayList<FoodDetailsinfo>()

        val foodList = easyMealDao.getMealsFoodById(mealId)
        for (foods in foodList){

            val foodName = foods.food_name
            val calories = foods.calories
            val servings = foods.servings
            val mealData = FoodDetailsinfo(foodName, servings,
                calories, mealId)

            mealDataList.add(mealData)


        }



        return mealDataList

    }

    suspend fun updateMealDetails(mealName: String, date:String, id: String){
        easyMealDao.updateMealDetails(mealName, id.toInt())
    }
    suspend fun updateCalories(calories:String, id: Int){

        easyMealDao.updateCalories(calories, id)
    }
    suspend fun updateServings(servings:String, id: Int){

        easyMealDao.updateServings(servings, id)
    }

}