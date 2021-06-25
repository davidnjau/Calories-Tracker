package com.centafrique.foodsurvey.RoomPersitence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uptech.easymeal.room_persitence.Entity.FoodDetailsinfo
import com.uptech.easymeal.room_persitence.Entity.Mealinfo


@Dao
interface EasyMealDao {

    @Query("SELECT * from meal_info")
    fun getMealLiveUrls(): LiveData<List<Mealinfo>>

    @Query("SELECT * from food_info")
    fun getFoodLiveUrls(): LiveData<List<FoodDetailsinfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeal(mealinfo: Mealinfo): Long

    @Query("DELETE FROM food_info WHERE id =:foodId")
    suspend fun deleteFoodItem(foodId: Int)

    @Query("DELETE FROM meal_info WHERE id =:mealId")
    suspend fun deleteMeal(mealId: Int)

    @Query("SELECT * from meal_info")
    suspend fun getMeals(): List<Mealinfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFoodMeal(foodDetailsinfo: FoodDetailsinfo)

    @Query("SELECT * from food_info WHERE mealId =:mealId")
    suspend fun getMealsFoodById(mealId: String): List<FoodDetailsinfo>

    @Query("SELECT * from meal_info WHERE id =:mealId")
    suspend fun getMealsById(mealId: String): Mealinfo

    @Query("UPDATE food_info SET servings =:servings WHERE id =:foodId")
    suspend fun updateServings(servings: String, foodId: Int)

    @Query("UPDATE food_info SET calories =:calories WHERE id =:foodId")
    suspend fun updateCalories(calories: String, foodId: Int)

    @Query("UPDATE meal_info SET meal_name =:meal_name WHERE id =:id")
    suspend fun updateMealDetails(meal_name: String, id: Int)

    @Query("SELECT * from food_info WHERE id =:foodId")
    suspend fun getFoodById(foodId: Int): FoodDetailsinfo
}

