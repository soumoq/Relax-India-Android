package org.relaxindia.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

object App {

    const val rs = "₹"
    const val locationAlert =
        "We'll only use your location to show available delivery option. You can change this preference in settings at any time"

    //Payment key id
    const val paymentkeyId = "rzp_test_jbQerQj2ziorxg"

    //google api key
    const val googleApiKey = "AIzaSyCxn_36SpMjIwCuoUSqOOI4N9E_6XTdJJk"

    //API
    const val apiBaseUrl = "http://itmartsolution.com/demo/relaxindia.org/api/v1/user/"
    const val apiLogin = "login"
    const val apiCheckOtp = "verify-otp"
    const val apiProfile = "profile"
    const val getService = "get-services"
    const val getSelectedService = "get-selected-services"
    const val getSaveBooking = "save-booking"
    const val getBookingHistory = "get-bookings"


    //Share preference key
    const val preferenceUserToken = "user_token"
    const val preferenceUserPhone = "user_phone"
    const val preferenceUserEmail = "user_email"
    const val preferenceUserName = "user_name"


    fun getUserToken(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return "Bearer ${(sp.getString(App.preferenceUserToken, "").toString())}"
    }

    fun getUserPhone(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(App.preferenceUserPhone, "")!!
    }

    fun getUserEmail(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(App.preferenceUserEmail, "")!!
    }

    fun getUserName(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(App.preferenceUserName, "")!!
    }


    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            locationMode = try {
                Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE)
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            !TextUtils.isEmpty(locationProviders)
        }
    }

    fun openLocationDialog(context: Context, title: String, message: String) {
        // setup the alert builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // add a button
        builder.setPositiveButton("Allow Access", DialogInterface.OnClickListener { dialog, which ->
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        })


        val dialog = builder.create()
        dialog.show()
    }

    fun openDialog(context: Context, title: String, message: String) {
        //val intent = Intent(context, TripAnalyzeActivity::class.java)
        //intent.putExtra("fileName", fileName)
        //context.startActivity(intent)


        // setup the alert builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // add a button
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->

        })

        // create and show the alert dialog

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }


    fun setNoteText(amount: String): String {
        return "Note: You have to pay $rs$amount Lorem Ipsum is simply dummy text of the printing and type setting industry. Lorem Ipsum has been the industry's standard."
    }

}