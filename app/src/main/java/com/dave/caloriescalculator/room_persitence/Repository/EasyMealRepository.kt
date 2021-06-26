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

    /**
     * Accesses the dao and passes the information to the view model,
     * observing the MVVM architecture
     */

    /**
     * Get the liveData as soon as there are changes in the sqlite
     */
    val urlsList: LiveData<List<FoodDetailsinfo>> = easyMealDao.getFoodLiveUrls()


    suspend fun addMeal(mealinfo: Mealinfo, context: Context){

        /**
         * Using Job() and coroutines to await a particular task to finish before proceeding
         */

        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {

            /**
             * Get the saved id and save the same in the shared preference
             */
            val sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.app_name),Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor =  sharedPreferences.edit()

            val id = easyMealDao.insertMeal(mealinfo)
            editor.putString("mealId",id.toString())
            editor.apply()


        }.join()
    }
    suspend fun insertFoodMeal(foodDetailsinfo: FoodDetailsinfo){

        /**
         * Using Job() and coroutines to await a particular task to finish before proceeding
         * Save food line and associate it with a meal
         */
        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {
            easyMealDao.insertFoodMeal(foodDetailsinfo)
        }.join()
    }

    suspend fun getMeals() : List<MealData>{
        /**
         * Get saved meals and associate them with their food line ids
         * calculate all calories and servings for a particular meal
         */

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

        /**
         * Get a saved date from a string of the format YYYY/MM/DD
         */

        val s1 = input.substring(input.indexOf("/") + 1)
        val s2 = s1.substring(s1.indexOf("/") + 1)

        return s2

    }
    private fun getNewCharacter(input: String):String{
        /**
         * Format string by removing the '.' after a number eg 20.888 becomes 20
         * for better viewing
         */
        return input.split(".")[0]
    }
    suspend fun deleteMeal(mealId: Int){
        /**
         * Delete meal and all foodlines with its id from room persitence
         */

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

        /**
         * Delete a particular food line item
         */
        return easyMealDao.deleteFoodItem(foodId)

    }

    suspend fun getMealsById(mealId : String) : Mealinfo{

        /**
         * Get the details of a meal by id
         */
        return easyMealDao.getMealsById(mealId)

    }
    suspend fun getFoodById(foodId : Int) : FoodDetailsinfo{
        /**
         * Get the details of a food line by id
         */
        return easyMealDao.getFoodById(foodId)

    }
    suspend fun getFoodMealsById(mealId : String) : List<FoodDetailsinfo>{
        /**
         * Get the details of a food by a meal id
         */
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
        /**
         * update meal details
         */
        easyMealDao.updateMealDetails(mealName, id.toInt())
    }
    suspend fun updateCalories(calories:String, id: Int){
        /**
         * update food calories for a particular food
         */
        easyMealDao.updateCalories(calories, id)
    }
    suspend fun updateServings(servings:String, id: Int){
        /**
         * update food servings for a particular food
         */
        easyMealDao.updateServings(servings, id)
    }

}