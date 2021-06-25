package com.dave.caloriescalculator.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dave.caloriescalculator.R
import com.dave.caloriescalculator.adapter.MyMealsAdapter
import com.dave.caloriescalculator.adapter.MyMealsEditAdapter
import com.dave.caloriescalculator.room_persitence.classes.FoodData
import com.uptech.easymeal.room_persitence.Entity.FoodDetailsinfo
import com.uptech.easymeal.room_persitence.ViewModel.EasyMealViewModel
import kotlinx.android.synthetic.main.activity_view_meal_details.*

class ViewMealDetails : AppCompatActivity() {

    private lateinit var easyMealViewModel: EasyMealViewModel
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var meal_data_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_meal_details)

        /**
         * User sees meal details and also is able to edit
         */

        easyMealViewModel = EasyMealViewModel(this.application)
        layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        val sharedPref: SharedPreferences =
            getSharedPreferences(resources.getString(R.string.app_name),
                Context.MODE_PRIVATE)

        btnDeleteMeal.setOnClickListener {

            /**
             * The method deletes all relationships associated with the meal including its foodlines
             */

            //Delete Meal
            if (meal_data_id != null){
                easyMealViewModel.deleteMeal(meal_data_id!!.toInt())

                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.remove("mealId")
                editor.remove("meal_data_id")
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

                Toast.makeText(this, "Deleted successfully..", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this, "Try again..", Toast.LENGTH_SHORT).show()

            }

        }

        recyclerViewFoods.layoutManager = layoutManager
        recyclerViewFoods.setHasFixedSize(true)


        btnAddFoodItem.setOnClickListener {

            /**
             * Add new meal items
             */

            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString("mealId", meal_data_id)
            editor.apply()

            val intent = Intent(this, AddFoodMeal::class.java)
            startActivity(intent)

        }

        btnUpdate.setOnClickListener {

            /**
             * Update database with new entry
             */

            //Update Meal items
            val mealName = tvMealName.text.toString()
            val mealDate = tvDate.text.toString()

            if (!TextUtils.isEmpty(mealName)
                && !TextUtils.isEmpty(mealDate)
                && meal_data_id != null){

                //Update data
                easyMealViewModel.updateMealDetails(mealName, mealDate, meal_data_id!!)
                Toast.makeText(this, "Meal updated.", Toast.LENGTH_SHORT).show()

            }else{

                if (TextUtils.isEmpty(mealName))
                    tvMealName.error = "Cannot be null.."
                if (TextUtils.isEmpty(mealDate))
                    tvDate.error = "Cannot be null.."

            }

        }


    }

    override fun onStart() {
        super.onStart()
        /**
         * Check for existing data and observe data from room persistence
         */

        val sharedPreferences = getSharedPreferences(
            getString(R.string.app_name), Context.MODE_PRIVATE)
        meal_data_id = sharedPreferences.getString("meal_data_id", null)
        if (meal_data_id != null){

            val mealDetails = easyMealViewModel.getMealsById(meal_data_id!!)
            val foodDetailsList = easyMealViewModel.getFoodMealsById(meal_data_id!!)

            tvMealName.setText(mealDetails.meal_name)
            val date = "${mealDetails.date} at ${mealDetails.time}"
            tvDate.text = date

            //LiveData
            easyMealViewModel.urlsfoodList.observe(this, { foodInfo: List<FoodDetailsinfo> ->
                this.prepareDetails(foodInfo) })


        }

    }

    private fun prepareDetails(foodInfoList: List<FoodDetailsinfo>) {

        /**
         * Display Live data from room database
         */

        val foodDetailsList = ArrayList<FoodData>()

        var caloriesNo = 0.0
        var servingsNo = 0.0

        for (foodData in foodInfoList){

            val mealId = foodData.mealId
            if (mealId == meal_data_id){

                val foodId = foodData.id!!
                val foodName = foodData.food_name
                val calories = foodData.calories
                val servings = foodData.servings

                caloriesNo += calories.toDouble()
                servingsNo += servings.toDouble()

                val foodDetailsinfo = FoodData(foodName, servings,
                    calories, mealId, foodId)
                foodDetailsList.add(foodDetailsinfo)
            }

        }
        /**
         * Check if there is an existing target and display it accordingly
         */


        val sharedPreferences = getSharedPreferences(
            getString(R.string.app_name),Context.MODE_PRIVATE)
        val targetCalories = sharedPreferences.getString("target_calories", null)

        val servings = "${getNewCharacter(servingsNo.toString())} Servings"
        var caloriesData = ""
        caloriesData = if (targetCalories != null){
            "${getNewCharacter(caloriesNo.toString())} / $targetCalories calories"
        }else{
            "${getNewCharacter(caloriesNo.toString())} / 0 calories"
        }

        if (targetCalories != null) {
            if(caloriesNo.toDouble() < targetCalories.toDouble()){
                tvCalories.setBackgroundResource(R.drawable.label_less)
            }else{
                tvCalories.setBackgroundResource(R.drawable.label_ok)
            }
        }else{
            tvCalories.setBackgroundResource(R.drawable.label_ok)
        }

        tvServings.text = servings
        tvCalories.text = caloriesData

        val mealsAdapter = MyMealsEditAdapter(
            application,
            foodDetailsList)
        recyclerViewFoods.adapter = mealsAdapter

    }
    private fun getNewCharacter(input: String):String{
        /**
         * Format the displayed data
         */
        return input.split(".")[0]
    }
}