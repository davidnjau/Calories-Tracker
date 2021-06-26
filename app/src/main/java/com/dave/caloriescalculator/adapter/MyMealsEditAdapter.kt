package com.dave.caloriescalculator.adapter

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.dave.caloriescalculator.R
import com.dave.caloriescalculator.helperClass.MyFoods
import com.dave.caloriescalculator.room_persitence.classes.FoodData
import com.squareup.picasso.Picasso
import com.uptech.easymeal.room_persitence.Entity.FoodDetailsinfo
import com.uptech.easymeal.room_persitence.ViewModel.EasyMealViewModel


class MyMealsEditAdapter(
    private val application: Application,
    private var myFoodList: List<FoodData>
    ) :
    RecyclerView.Adapter<MyMealsEditAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val tvMealName : TextView = itemView.findViewById(R.id.tvMealName)
        val tvCalories : TextView = itemView.findViewById(R.id.tvCalories)
        val tvServings : TextView = itemView.findViewById(R.id.tvServings)
        val imageView : ImageView = itemView.findViewById(R.id.imageView)
        val linearDelete : LinearLayout = itemView.findViewById(R.id.linearAdd)

        val easyMealViewModel = EasyMealViewModel(application)

        init {

            itemView.setOnClickListener (this)
            linearDelete.setOnClickListener (this)

        }

        override fun onClick(v: View?) {

            val position = adapterPosition
            val food_name = myFoodList[position].food_name
            val calories = myFoodList[position].calories
            val servings = myFoodList[position].servings
            val foodId = myFoodList[position].foodId

            val id = v?.id
            if (id == R.id.linearAdd){
                /**
                 * Handle onClick event, delete food item.
                 */
                easyMealViewModel.deleteFoodItem(foodId)
                Toast.makeText(application.applicationContext, "$food_name has been deleted", Toast.LENGTH_SHORT).show()

            }else{

                /**
                 * Handle onClick event, Display food item details for editing
                 */
                val builder = AlertDialog.Builder(
                    v!!.context
                )
                val view: View = LayoutInflater.from(application.applicationContext)
                    .inflate(R.layout.food_edit_dialog, null)

                val tvMealName = view.findViewById<View>(R.id.tvMealName) as TextView
                val tvCalories = view.findViewById<View>(R.id.tvCalories) as TextView
                val tvServings = view.findViewById<View>(R.id.tvServings) as TextView
                tvCalories.text = calories
                tvServings.text = servings
                tvMealName.text = food_name

                val btnReduceCalories = view.findViewById<View>(R.id.btnReduceCalories) as Button
                val btnAddCalories = view.findViewById<View>(R.id.btnAddCalories) as Button

                val btnReduceServings = view.findViewById<View>(R.id.btnReduceServings) as Button
                val btnAddServings = view.findViewById<View>(R.id.btnAddServings) as Button

                /**
                 * Handle, individual clicks, return saved value and display data on textview
                 * The values are also updated on the recyclerview using LiveData
                 */

                btnAddCalories.setOnClickListener {
                    val value = calculator( "add", foodId,"calories")
                    tvCalories.text = value
                }
                btnReduceCalories.setOnClickListener {
                    val value = calculator( "sub", foodId,"calories")
                    tvCalories.text = value
                }

                btnAddServings.setOnClickListener {
                    val value = calculator("add", foodId,"servings")
                    tvServings.text = value
                }
                btnReduceServings.setOnClickListener {
                    val value =  calculator("sub", foodId,"servings")
                    tvServings.text = value
                }

                builder.setView(view)
                builder.setNegativeButton("back") { dialog, _ -> dialog.dismiss()
                }
                val alertDialog = builder.create()
                alertDialog.show()

            }


            
        }


    }

    private fun calculator(action: String, foodId:Int, type: String):String {

        /**
         * Handle the sub-sequest clicks for editing; either addition or reduction of
         * calories or servings
         */

        val easyMealViewModel = EasyMealViewModel(application)

        //Fetch New Values
        val foodDetailsinfo = easyMealViewModel.getFoodById(foodId)
        val servings = foodDetailsinfo.servings.toDouble()
        val calories = foodDetailsinfo.calories.toDouble()

        var value = ""

        if (type == "calories"){

            if (calories >= 1){

                /**
                 * Cannot update anything less than 1
                 * Check the passed action; addition & subtraction.
                 * Perform the action and update in room persitence
                 */

                if (action == "add"){
                    val newValue = calories + 1
                    easyMealViewModel.updateCalories(newValue.toString(), foodId)
                    value = newValue.toString()
                }
                if (action == "sub"){
                    value = if (calories > 1){
                        val newValue = calories - 1
                        easyMealViewModel.updateCalories(newValue.toString(), foodId)
                        newValue.toString()
                    }else{
                        calories.toString()
                    }

                }

            }else{
                value = calories.toString()

            }

        }
        if (type == "servings"){
            if (servings >= 1){
                /**
                 * Cannot update anything less than 1
                 * Check the passed action; addition & subtraction.
                 * Perform the action and update in room persitence
                 */
                if (action == "add"){
                    val newValue = servings + 1
                    easyMealViewModel.updateServings(newValue.toString(), foodId)
                    value = newValue.toString()
                }
                if (action == "sub"){
                    value = if (servings > 1){
                        val newValue = servings - 1
                        easyMealViewModel.updateServings(newValue.toString(), foodId)
                        newValue.toString()
                    }else{
                        servings.toString()

                    }

                }

            }else{
                value = servings.toString()

            }
        }


        return value


    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.foods_edit,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {

        val mealName = myFoodList[position].food_name
        val calories = "${myFoodList[position].calories} Cal"
        val servings = "${myFoodList[position].servings} Servings"
        val mealId = myFoodList[position].mealId

        /**
         * Populate data to respective fields
         */

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