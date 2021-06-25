package com.dave.caloriescalculator.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.EventDay
import com.dave.caloriescalculator.R
import com.dave.caloriescalculator.adapter.AllMealsAdapter
import com.dave.caloriescalculator.adapter.MyMealsAdapter
import com.uptech.easymeal.room_persitence.ViewModel.EasyMealViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var easyMealViewModel: EasyMealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Launcher activity, user sees all added meals and their details
         */

        easyMealViewModel = EasyMealViewModel(this.application)

        val events: MutableList<EventDay> = ArrayList()
        val sharedPreferences = this.getSharedPreferences(resources.getString(R.string.app_name),
            Context.MODE_PRIVATE)

        layoutManager = GridLayoutManager(
            this,
            2
        )
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)


        fab.setOnClickListener {

            val editor =  sharedPreferences.edit()
            editor.putString("calenderDate", "")
            editor.apply()

            val intent = Intent(this, AddMeal::class.java)
            startActivity(intent)

        }

        settings.setOnClickListener {


            val intent = Intent(this, Settings::class.java)
            startActivity(intent)

        }

    }

    override fun onStart() {
        super.onStart()

        val mealDataList = easyMealViewModel.getMealDataList()
        if(mealDataList.isNotEmpty()){
            linearInfo.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }else{
            linearInfo.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }

        val allMealsAdapter = AllMealsAdapter(
            mealDataList,
            this
        )
        recyclerView.adapter = allMealsAdapter
    }
}