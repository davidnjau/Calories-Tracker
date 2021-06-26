package com.uptech.easymeal.room_persitence.ViewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.uptech.easymeal.room_persitence.Database.EasyMealRoomDatabase
import com.uptech.easymeal.room_persitence.Entity.FoodDetailsinfo
import com.uptech.easymeal.room_persitence.Entity.Mealinfo
import com.uptech.easymeal.room_persitence.Repository.EasyMealRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EasyMealViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Gets data from the repository observing the MVVM architecture
     * all methods here are quite the same with the ones in the repository,
     * as these are just for passing parameters and receiving results
     */

    private val repository: EasyMealRepository
    val urlsfoodList: LiveData<List<FoodDetailsinfo>>

    init {
        val dynamicDao = EasyMealRoomDatabase.getDatabase(application).easyMeal()
        repository = EasyMealRepository(dynamicDao)
        urlsfoodList = repository.urlsList

    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */

//    fun getMeals() = runBlocking{
//        repository.getMeals()
//    }
//
    fun getMealsById(foodId:String) = runBlocking{
        repository.getMealsById(foodId)
    }
    fun getFoodById(foodId:Int) = runBlocking{
        repository.getFoodById(foodId)
    }
    fun getFoodMealsById(foodId:String) = runBlocking{
        repository.getFoodMealsById(foodId)
    }
    fun addMeal(mealinfo: Mealinfo, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            repository.addMeal(mealinfo, context)

        }
    }
    fun updateMealDetails(mealName: String, date:String, id: String){
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateMealDetails(mealName, date, id)

        }
    }
    fun updateCalories(calories: String, id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateCalories(calories, id)

        }
    }
    fun updateServings(servings: String, id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateServings(servings, id)

        }
    }
    fun insertFoodMeal(foodDetailsinfo: FoodDetailsinfo){
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertFoodMeal(foodDetailsinfo)

        }
    }

    fun getMealDataList()= runBlocking {
        repository.getMeals()
    }
    fun deleteFoodItem(foodId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteFoodItem(foodId)

        }
    }
    fun deleteMeal(mealId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteMeal(mealId)

        }
    }





}