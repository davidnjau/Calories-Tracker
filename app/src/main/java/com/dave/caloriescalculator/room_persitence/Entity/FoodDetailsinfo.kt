package com.uptech.easymeal.room_persitence.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_info")
data class FoodDetailsinfo(
        /**
         * Create tables in the db by using the entity annotation,
         * indicate the columns and assign a PK
         */

        val food_name: String,
        val servings: String,
        val calories: String,
        val mealId: String
        ){
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null
}