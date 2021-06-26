package com.dave.caloriescalculator.room_persitence.classes

data class FoodData(
        /**
         * A simple data class to be used for storing data
         */

        val food_name: String,
        val servings: String,
        val calories: String,
        val mealId: String,
        val foodId: Int
)