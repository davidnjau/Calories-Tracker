package com.dave.caloriescalculator.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dave.caloriescalculator.R
import com.dave.caloriescalculator.adapter.MyMealsAdapter
import com.dave.caloriescalculator.helperClass.MyFoods
import com.dave.caloriescalculator.network_request.call_requests.RetrofitCallsNutrinix
import com.uptech.easymeal.room_persitence.Entity.FoodDetailsinfo
import com.uptech.easymeal.room_persitence.ViewModel.EasyMealViewModel
import kotlinx.android.synthetic.main.activity_add_food_meal.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddFoodMeal : AppCompatActivity() {

    private lateinit var recyclerViewFoods : RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    val foodCommonList = ArrayList<MyFoods>()
    val foodBrandedList = ArrayList<MyFoods>()
    var myScannedResult: String? = null
    var myMealId: String? = null
    private lateinit var easyMealViewModel: EasyMealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food_meal)

        /**
         * The Activity enables users to add new food lines to the already existing meal.
         */

        easyMealViewModel = EasyMealViewModel(this.application)

        btnFinish.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        recyclerViewFoods = findViewById(R.id.recyclerViewFoods)
        layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerViewFoods.layoutManager = layoutManager
        recyclerViewFoods.setHasFixedSize(true)

        imgBtnScanCode.setOnClickListener {

            val intent = Intent(this, ScanQrCode::class.java)
            startActivity(intent)
            finish()
        }

        btnSearchFood.setOnClickListener {

            foodCommonList.clear()
            foodBrandedList.clear()

            val queryItem = autoCompleteTextView1.text.toString()
            fetchData(queryItem)
            setProgressDialog("start")


        }



        autoCompleteTextView1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val queryItem = s.toString()
                setProgressDialog("start")
                getAutoCompleteData(queryItem)
            }
        })

        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            populateData()

        }

    }

    private fun getAutoCompleteData(queryItem: String) {

        /**
         * Search for data when its been populated using the text watcher and search from provided endpoint
         */
        CoroutineScope(Dispatchers.IO).launch {

            val listPair = RetrofitCallsNutrinix().fetchQuery(this@AddFoodMeal,queryItem)
            val commonDataList = listPair.first
            val brandedDataList = listPair.second

            val autoCompleteList = ArrayList<String>()

            for (commonItems in commonDataList){ autoCompleteList.add(commonItems.food_name) }
            for (brandedItems in brandedDataList){ autoCompleteList.add(brandedItems.food_name) }
            CoroutineScope(Dispatchers.Main).launch { fillDataList(autoCompleteList) }



        }




    }

    private fun fillDataList(autoCompleteList: List<String>) {

        /**
         *Display returned food lines from endpoint
         */

        val adapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this@AddFoodMeal, android.R.layout.simple_list_item_1,
                autoCompleteList)
        autoCompleteTextView1.setAdapter(adapter);
        autoCompleteTextView1.threshold = 1

        setProgressDialog("end")

    }

    private fun fetchData(queryItem: String) {

        /**
         * Search for data that has been populated in the autocomplete field
         */

        CoroutineScope(Dispatchers.IO).launch {

            val listPair = RetrofitCallsNutrinix().fetchQuery(this@AddFoodMeal,queryItem)
            val commonDataList = listPair.first
            val brandedDataList = listPair.second

            for(commonItems in commonDataList){
                val foodName = commonItems.food_name
                val serveQty = commonItems.serving_qty
                val myFoods = MyFoods(foodName, "-", serveQty, "")
                foodCommonList.add(myFoods)
            }

            for(brandedList in brandedDataList){
                val foodName = brandedList.food_name
                val serveQty = brandedList.serving_qty
                val caloriesNo = brandedList.nf_calories
                val myFoods = MyFoods(foodName, caloriesNo, serveQty, "")
                foodBrandedList.add(myFoods)
            }

            populateData()


        }



    }


    private fun populateData() {

        /**
         * Display foods lines depending on the selected radio button
         */


        CoroutineScope(Dispatchers.Main).launch {

            val commonValue = "Common Foods (${foodCommonList.size})"
            val brandedValue = "Branded Foods (${foodBrandedList.size})"

            radioBtnCommon.text = commonValue
            radioBtnBranded.text = brandedValue

            val id: Int = radioGroup.checkedRadioButtonId
            val radio: RadioButton = findViewById(id)
            val radioButtonText = radio.text.toString()

            if (radioButtonText.contains("Common")){

                val ordersAdapter = MyMealsAdapter(
                    application,
                    foodCommonList,
                    myMealId!!
                )
                recyclerViewFoods.adapter = ordersAdapter

            }
            if (radioButtonText.contains("Branded")){


                val ordersAdapter = MyMealsAdapter(
                    application,
                    foodBrandedList,
                    myMealId!!

                )
                recyclerViewFoods.adapter = ordersAdapter


            }

            setProgressDialog("end")



        }

    }

    override fun onStart() {
        super.onStart()

        /**
         * Check if there is an existing meal id, if not we cannot add food lines to a null meal
         * return user so that they add a meal.
         */

        val sharedPreferences = this.getSharedPreferences( resources.getString(R.string.app_name),
            Context.MODE_PRIVATE)
        myMealId = sharedPreferences.getString("mealId",null)
        if (myMealId == null){
            val intent = Intent(this, AddMeal::class.java)
            startActivity(intent)
            finish()
        }

        checkData()

    }

    private fun checkData() {

        /**
         * Happens when the user navigates from SCANNING the QR code.
         * Check if there is a passed data from the previous activity, if there's valid data, search
         * endpoint for the upc.
         * If valid data is returned, we add it to the offline storage. If not we inform user.
         */

        val scannedResult:String? = intent.getStringExtra("scannedResult")
        if (scannedResult != null){

            if (scannedResult != ""){

                myScannedResult = scannedResult.toString()
                if (myScannedResult != null){
                    val listPair = RetrofitCallsNutrinix()
                        .fetchUsingQRCode(this@AddFoodMeal,
                        myScannedResult!!
                    )
                    if (listPair.isNotEmpty()) {

                        for (items in listPair) {

                            val foodName = items.food_name
                            val calories = items.calories
                            val servings = items.servings

                            val foodDetailsinfo =
                                myMealId?.let { FoodDetailsinfo(foodName, servings, calories, it) }

                            if (foodDetailsinfo != null) {
                                easyMealViewModel.insertFoodMeal(foodDetailsinfo)
                                Toast.makeText(
                                    this,
                                    "$foodName has been added successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        }
                    }else{
                        Toast.makeText(this, "Check the barcode and try again.", Toast.LENGTH_SHORT).show()

                    }


                }


            }

        }
    }


    private fun setProgressDialog(action: String){
        /**
         *     Display simple progress dialog when entering and searching new food lines
         */
        if (action == "start")
            linearProgress.visibility = View.VISIBLE
        if (action == "end")
            linearProgress.visibility = View.GONE
    }

}