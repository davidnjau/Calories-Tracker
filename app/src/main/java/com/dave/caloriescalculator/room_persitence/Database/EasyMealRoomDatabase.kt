package com.uptech.easymeal.room_persitence.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.centafrique.foodsurvey.RoomPersitence.EasyMealDao
import com.uptech.easymeal.room_persitence.Entity.FoodDetailsinfo
import com.uptech.easymeal.room_persitence.Entity.Mealinfo

@Database(

    /**
     * Create a database instance of the room database
     * Include all entities you will use
     * Associate the db with a dao that contains the sql statements
     * Have a version for the current db version
     */
        entities = [
            Mealinfo::class, FoodDetailsinfo::class
        ],
        version = 1,
        exportSchema = false

)

abstract class EasyMealRoomDatabase : RoomDatabase() {
     abstract fun easyMeal() : EasyMealDao
     companion object{
         @Volatile
         private var INSTANCE : EasyMealRoomDatabase? = null
         fun getDatabase(context: Context) : EasyMealRoomDatabase {
             val tempInstance = INSTANCE
             if (tempInstance != null){
                 return tempInstance
             }
             synchronized(this){
                 val instance = Room.databaseBuilder(
                     context.applicationContext,
                     EasyMealRoomDatabase::class.java,
                     "easy_meal_db"
                 ).build()
                 INSTANCE = instance
                 return instance
             }
         }
     }

}