package com.dave.caloriescalculator.room_persitence.classes

data class MealData(
        /**
         * A simple data class to be used for storing data
         */
        val mealId : String,
        val date: String,
        val mealName: String,
        val caloriesNumber: String,
        val servingsNumber: String,
        val foodItemsNumber: String
)