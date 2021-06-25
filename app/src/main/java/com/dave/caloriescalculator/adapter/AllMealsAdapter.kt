package com.dave.caloriescalculator.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dave.caloriescalculator.R
import com.dave.caloriescalculator.activity.MainActivity
import com.dave.caloriescalculator.activity.ViewMealDetails
import com.dave.caloriescalculator.room_persitence.classes.MealData

class AllMealsAdapter(
    private var mealInfoList: List<MealData>,
    private val context: Context
) :
    RecyclerView.Adapter<AllMealsAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val tvMealName : TextView = itemView.findViewById(R.id.tvMealName)
        val tvCalories : TextView = itemView.findViewById(R.id.tvCalories)
        val tvServings : TextView = itemView.findViewById(R.id.tvServings)
        val tvFoodNumber : TextView = itemView.findViewById(R.id.tvFoodNumber)
        val tvDate : TextView = itemView.findViewById(R.id.tvDate)
        val tvSuffix : TextView = itemView.findViewById(R.id.tvSuffix)



        init {

            itemView.setOnClickListener(this)

        }

        override fun onClick(v: View?) {

            /**
             * Handle onClick event, and navigate to view Meal details
             */

            val sharedPref: SharedPreferences =
                context.getSharedPreferences(context.resources.getString(R.string.app_name),
                    Context.MODE_PRIVATE)

            val position = adapterPosition
            val mealId = mealInfoList[position].mealId

            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString("meal_data_id", mealId)
            editor.apply()

            val intent = Intent(context, ViewMealDetails::class.java)
            context.startActivity(intent)

        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_view_meals,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {

        /**
         * Fetch data passed to the adapter
         */
        val mealName = mealInfoList[position].mealName
        val calories = mealInfoList[position].caloriesNumber
        val servings = "${mealInfoList[position].servingsNumber} Servings"
        val foodItemsNumber = mealInfoList[position].foodItemsNumber
        val mealDate = mealInfoList[position].date

        val dateSuffix = getDateSuffix(mealDate.substring(mealDate.length - 1))

        holder.tvMealName.text = mealName
        holder.tvServings.text = servings
        holder.tvFoodNumber.text = foodItemsNumber
        holder.tvDate.text = mealDate
        holder.tvSuffix.text = dateSuffix

        /**
         * Check for calories target from the shared preferences and display it accordingly
         */
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.app_name),Context.MODE_PRIVATE)
        val targetCalories = sharedPreferences.getString("target_calories", null)

        var caloriesData = ""
        caloriesData = if (targetCalories != null){
            "$calories/$targetCalories calories"
        }else{
            "$calories/0 calories"
        }

        if (targetCalories != null) {
            if(calories.toDouble() < targetCalories.toDouble()){ holder.tvCalories.setBackgroundResource(R.drawable.label_less)
            }else{ holder.tvCalories.setBackgroundResource(R.drawable.label_ok)
            }
        }
        holder.tvCalories.text = caloriesData



    }

    override fun getItemCount(): Int {
        return mealInfoList.size
    }

    private fun getDateSuffix(date:String):String{
        /**
         * Display the date with the correct suffix
         */
        var suffix = ""
        suffix = when (date) {
            "1" -> {
                "st"
            }
            "2" -> {
                "nd"
            }
            "3" -> {
                "rd"
            }
            "4", "5", "6", "7", "8", "9", "0" -> {
                "th"
            }
            else -> {
                ""
            }
        }

        return suffix

    }

}