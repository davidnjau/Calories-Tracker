package com.dave.caloriescalculator.adapter

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dave.caloriescalculator.R
import com.dave.caloriescalculator.helperClass.MyFoods
import com.dave.caloriescalculator.network_request.call_requests.RetrofitCallsNutrinix
import com.squareup.picasso.Picasso
import com.uptech.easymeal.room_persitence.Entity.FoodDetailsinfo
import com.uptech.easymeal.room_persitence.ViewModel.EasyMealViewModel
import java.util.HashMap

class MyMealsAdapter(
    private val application: Application,
    private var myFoodList: List<MyFoods>,
    private val mealId:String

    ) :
    RecyclerView.Adapter<MyMealsAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val tvMealName : TextView = itemView.findViewById(R.id.tvMealName)
        val tvCalories : TextView = itemView.findViewById(R.id.tvCalories)
        val tvServings : TextView = itemView.findViewById(R.id.tvServings)
        val imageView : ImageView = itemView.findViewById(R.id.imageView)
        val linearAdd : LinearLayout = itemView.findViewById(R.id.linearAdd)

        val easyMealViewModel = EasyMealViewModel(application)

        init {

//            itemView.setOnClickListener (this)
            linearAdd.setOnClickListener (this)

        }

        override fun onClick(v: View?) {

            /**
             * Handle onClick event, and insert food lines
             */

            val position = adapterPosition
            val mealName = myFoodList[position].mealName
            val caloriesNo = myFoodList[position].caloriesNo
            val servingsNo = myFoodList[position].servings

            if (caloriesNo == "-"){
                /**
                 * Hit the nutrients endpoint for common foods and get their details.
                 */

                val foodNameDetails = RetrofitCallsNutrinix()
                    .fetchCommonMealData(application.applicationContext,mealName)
                val foodName = foodNameDetails.food_name
                val calories = foodNameDetails.nf_calories
                val servings = foodNameDetails.serving_qty

                val foodDetailsinfo = FoodDetailsinfo(foodName, servings, calories, mealId)
                easyMealViewModel.insertFoodMeal(foodDetailsinfo)
                Toast.makeText(application.applicationContext, "$foodName item saved..", Toast.LENGTH_SHORT).show()

            }else{

                val foodDetailsinfo = FoodDetailsinfo(mealName, servingsNo, caloriesNo, mealId)
                easyMealViewModel.insertFoodMeal(foodDetailsinfo)
                Toast.makeText(application.applicationContext, "$mealName item saved..", Toast.LENGTH_SHORT).show()


            }




        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.foods,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {

        val mealName = myFoodList[position].mealName
        val calories = "${myFoodList[position].caloriesNo} Cal"
        val servings = "${myFoodList[position].servings} Servings"
        val mealImage = myFoodList[position].imageUrl

        holder.tvMealName.text = mealName
        holder.tvCalories.text = calories
        holder.tvServings.text = servings

//        Picasso.get().load(mealImage).into(
//            holder.imageView
//        )

    }

    override fun getItemCount(): Int {
        return myFoodList.size
    }


}