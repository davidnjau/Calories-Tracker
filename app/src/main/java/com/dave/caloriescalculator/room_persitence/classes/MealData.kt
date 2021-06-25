package com.dave.caloriescalculator.room_persitence.classes

data class MealData(

        val mealId : String,
        val date: String,
        val mealName: String,
        val caloriesNumber: String,
        val servingsNumber: String,
        val foodItemsNumber: String
)