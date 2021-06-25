package com.uptech.easymeal.room_persitence.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_info")
data class Mealinfo(

        val meal_name: String,
        val date: String,
        val time: String
        ){
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null
}