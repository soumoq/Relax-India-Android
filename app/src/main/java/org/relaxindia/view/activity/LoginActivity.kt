package org.relaxindia.view.activity

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_login.*
import org.relaxindia.R
import org.relaxindia.util.App
import org.relaxindia.util.toast
import org.relaxindia.viewModel.ApiCallViewModel

class LoginActivity : AppCompatActivity() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    lateinit var apiCallViewModel: ApiCallViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (App.isLocationEnabled(this)) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            val sp = applicationContext.getSharedPreferences("user_info", MODE_PRIVATE)
            val id = sp.getString(App.preferenceUserToken, "")
            if (!(id.equals(""))) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        } else {
            displayLocationSettingsRequest(this)
        }

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()
        val text =
            "By Continuing, you agree to the <font color=#1b9ff1>Terms Of Service </font>and <font color=#1b9ff1>Privacy Policy</font>"
        terms_service.text = Html.fromHtml(text)

        login_continue.button.setOnClickListener {

            if (login_phone_number.text.toString().length == 10) {
                apiCallViewModel.loginInfo(this, login_phone_number.text.toString())
            } else {
                login_phone_number.error = "Enter valid phone number"
            }

        }
    }

    private fun observeViewModel() {
        apiCallViewModel.loginInfo.observe(this, Observer {
            if (!it.error) {
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra("phone_number", login_phone_number.text.toString())
                intent.putExtra("otp", it.data.otp.toString())
                startActivity(intent)
            } else {
                toast("Something went wrong")
            }
        })
    }


    override fun onResume() {
        super.onResume()

    }

    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i(
                        "LOCATON_TAG",
                        "All location settings are satisfied."
                    )
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        "LOCATON_TAG",
                        "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                    )
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(this, 1)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.i("LOCATON_TAG", "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                    "LOCATON_TAG",
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (App.isLocationEnabled(this)) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            val sp = applicationContext.getSharedPreferences("user_info", MODE_PRIVATE)
            val id = sp.getString(App.preferenceUserToken, "")
            if (!(id.equals(""))) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        } else {
            displayLocationSettingsRequest(this)
        }

    }



    override fun onStart() {
        super.onStart()

    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}