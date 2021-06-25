package com.dave.caloriescalculator.activity

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dave.caloriescalculator.R
import com.uptech.easymeal.room_persitence.Entity.Mealinfo
import com.uptech.easymeal.room_persitence.ViewModel.EasyMealViewModel
import kotlinx.android.synthetic.main.activity_add_meal.*
import java.util.*


class AddMeal : AppCompatActivity() {

    var hour: Int = 0
    var minute: Int = 0
    var myDate: String? = null
    var myDate1: String? = null
    var myTime: String? = null

    lateinit var mTimePicker: TimePickerDialog

    private lateinit var easyMealViewModel: EasyMealViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)

        /**
         * User Adds new Meals
         */

        easyMealViewModel = EasyMealViewModel(this.application)

        btnAddFoodItem.setOnClickListener {

            val mealName = etMealName.text.toString()
            if (!TextUtils.isEmpty(mealName) && myDate1 != null && myTime != null){

                /**
                 * Adds meal to offline storage
                 */

                val mealInfo = Mealinfo(mealName, myDate1!!, myTime!!)
                easyMealViewModel.addMeal(mealInfo, this)

                val intent = Intent(this, AddFoodMeal::class.java)
                startActivity(intent)

            }else{

                if(TextUtils.isEmpty(mealName))
                    etMealName.error = "This field cannot be empty.."

                if (myDate1 == null)
                    Toast.makeText(this, "Please add date..", Toast.LENGTH_SHORT).show()

                if (myTime == null)
                    Toast.makeText(this, "Please add time..", Toast.LENGTH_SHORT).show()


            }




        }

        linearTime.setOnClickListener {

            /**
             * User gets to pick time
             */

            hour = 0
            minute = 0

            myTime = null
            tvTime.text = ""

            val calendar: Calendar = Calendar.getInstance()
            hour = calendar.get(Calendar.HOUR)
            minute = calendar.get(Calendar.MINUTE)

            mTimePicker.show()

        }

        mTimePicker = TimePickerDialog(this,
            { _, hourOfDay, minute ->
                val timeString = "$hourOfDay:$minute"
                myTime = timeString
                tvTime.text = timeString
            }, hour, minute, false)

        linearDate.setOnClickListener {

            /**
             * User chooses date
             */
            myDate = null
            tvDate.text = ""

            hour = 0
            minute = 0

            calendarView.visibility = View.VISIBLE



        }

        calendarView.setOnDayClickListener { eventDay ->

            /**
             * Choose date
             */
            val clickedDayCalendar = eventDay.calendar
            val dateString1 = clickedDayCalendar.timeInMillis

            myDate = clickedDayCalendar.toString()

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dateString1

            val mYear = calendar[Calendar.YEAR]
            val mMonth = calendar[Calendar.MONTH]
            val mDay = calendar[Calendar.DAY_OF_MONTH]

            val dateString = "$mYear/$mMonth/$mDay"
            tvDate.text= dateString
            myDate1 = dateString

            if (myDate != null)
                calendarView.visibility = View.GONE

        }



    }
    override fun onStart() {
        super.onStart()

        /**
         * Check for existence data
         */

        val sharedPreferences = this.getSharedPreferences( resources.getString(R.string.app_name),
            Context.MODE_PRIVATE)
        val calenderDate = sharedPreferences.getString("calenderDate",null)

        if (calenderDate != null){

            if (calenderDate != ""){

                tvDate.text = calenderDate

            }

        }

    }



}