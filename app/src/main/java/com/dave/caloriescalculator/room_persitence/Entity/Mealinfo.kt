package com.uptech.easymeal.room_persitence.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_info")
data class Mealinfo(
        /**
         * Create tables in the db by using the entity annotation,
         * indicate the columns and assign a PK
         */
        val meal_name: String,
        val date: String,
        val time: String
        ){
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null
}