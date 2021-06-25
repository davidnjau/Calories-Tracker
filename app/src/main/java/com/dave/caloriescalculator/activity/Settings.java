package com.dave.caloriescalculator.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.EditText;

import com.dave.caloriescalculator.R;

public class Settings extends AppCompatActivity {

    private EditText etCaloriesTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /**
        User adds calories target to shared preferences
         */

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        etCaloriesTarget = findViewById(R.id.etCaloriesTarget);

        findViewById(R.id.btnSubmit).setOnClickListener(v -> {

            String caloriesTarget = etCaloriesTarget.getText().toString();
            if (!TextUtils.isEmpty(caloriesTarget)){

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("target_calories", caloriesTarget);
                editor.apply();

                startActivity(new Intent(this, MainActivity.class));

            }else {

                etCaloriesTarget.setError("Calories cannot be null");
            }

        });
    }
}