package com.dave.caloriescalculator.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.dave.caloriescalculator.R
import kotlinx.android.synthetic.main.activity_scan_qr_code.*

class ScanQrCode : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private val MY_PERMISSIONS_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr_code)

        /**
         * User Scans QRCode and returns result to AddFoodMeal
         */

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {

                val scannedResult = it.text
                val intent = Intent(baseContext, AddFoodMeal::class.java)
                intent.putExtra("scannedResult", scannedResult)
                startActivity(intent)

            }
        }

        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {

                checkForPermission()


            }
        }

        btnScan.setOnClickListener {

            codeScanner.startPreview()

        }
        btnCancel.setOnClickListener {

            val intent = Intent(baseContext, AddFoodMeal::class.java)
            intent.putExtra("scannedResult", "none")
            startActivity(intent)

        }

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()

        checkForPermission()



    }

    private fun checkForPermission() {

        /**
         * Check for camera permission
         */

        if (
            ContextCompat.checkSelfPermission(this@ScanQrCode, Manifest.permission.CAMERA)
            +
            ContextCompat.checkSelfPermission(this@ScanQrCode, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            != PackageManager.PERMISSION_GRANTED
        ) {

            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@ScanQrCode, Manifest.permission.CAMERA)
                ||
                ActivityCompat.shouldShowRequestPermissionRationale(this@ScanQrCode, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            ) {
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                val builder = AlertDialog.Builder(this@ScanQrCode)
                builder.setMessage(
                    "Read and Write External" +
                            " Storage permissions are required for the app."
                )
                builder.setTitle("Please grant these permissions")
                builder.setPositiveButton(
                    "OK"
                ) { _, _ ->
                    ActivityCompat.requestPermissions(this@ScanQrCode,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE

                        ),
                        MY_PERMISSIONS_REQUEST_CODE
                    )
                }
                builder.setNeutralButton(
                    "Cancel"
                ) { _, _ ->
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                    this@ScanQrCode, arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    MY_PERMISSIONS_REQUEST_CODE
                )
            }
        }

    }
}