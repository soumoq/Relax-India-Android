package org.relaxindia.util

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import org.json.JSONArray
import org.json.JSONObject
import org.relaxindia.model.NotificationDataModel


object App {

    var ambulanceSearchRedis: String = "5"

    const val RS = "₹"
    const val STORE_URL = "https://www.relaxindia.org/"
    const val locationAlert =
        "We'll only use your location to show available delivery option. You can change this preference in settings at any time"

    //Payment key id
    const val PAYMENT_KAY_ID = "rzp_test_3wnATNB0dnwOxX"

    //google api key
    const val googleApiKey = "AIzaSyCxn_36SpMjIwCuoUSqOOI4N9E_6XTdJJk"

    //API
    const val apiBaseUrl = "https://recztrade.com/demo/relaxindia.org/api/v1/user/"
    const val apiLogin = "login"
    const val apiCheckOtp = "verify-otp"
    const val API_PROFILE = "profile"
    const val getService = "get-services"
    const val getSelectedService = "get-selected-services"
    const val getSaveBooking = "save-booking"
    const val updateBooking = "update-booking"
    const val getBookingHistory = "get-bookings"
    const val getAllDrivers = "get-all-drivers"
    const val UPDATE_DEVICE_TOKEN = "update-device-token"
    const val FIND_AMBULANCE = "find-ambulance"
    const val CANCEL_BOOKING = "cancel-booking"
    const val SCHEDULE_REQUEST = "schedule-request"
    const val ALL_SCHEDULE_REQ = "get-schedule-requests"
    const val UPDATE_SCHEDULE_REQ = "save-schedule-booking"
    const val GET_SCHEDULE_BOOKING = "get-schedule-bookings"
    const val RAISE_TOKEN = "raise-ticket"
    const val GET_TICKETS = "get-tickets"
    const val RATE_DRIVER = "rate-driver"
    const val GET_RATING = "get-driver-rating"
    const val GET_TRANSACTION = "get-transactions"
    const val SAVE_SEARCH = "save-search"
    const val GET_GUIDE = "get-app-tour-guide-video"
    const val NEAR_HOSPITAL = "get-nearby-hospitals"


    //Share preference key
    const val preferenceUserToken = "user_token"
    const val preferenceUserPhone = "user_phone"
    const val preferenceUserEmail = "user_email"
    const val preferenceUserName = "user_name"
    const val preferenceUserId = "user_id"


    //Notification
    const val serverKey =
        "key=" + "AAAA1CWxbXI:APA91bGbT-na_V9dGiYNbIHUY7xj2g7GEJaZV3yCYoaqqIkVGzzutKBDWCjt5QeEAGF4tv5WaqcNB3KXrJ4rxGzXg8iMpdKAc5Q1pfHTWlNe4JV9JWRqndlw7FpE1tB-Dkn0tyEFuLLv"
    const val contentType = "application/json"
    const val FCM_API = "https://fcm.googleapis.com/fcm/send"

    fun sendNotification(
        context: Context,
        array: ArrayList<String>,
        notificationData: NotificationDataModel
    ) {

        val requestQueue: RequestQueue by lazy {
            Volley.newRequestQueue(context)
        }

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        notifcationBody.put("title", "New Request")
        notifcationBody.put(
            "message",
            "A new patient found. Please accept or reject to click hare."
        )
        // notification message
        notifcationBody.put("booking_id", notificationData.bookingId)
        notifcationBody.put("source_loc", notificationData.sourceLoc)
        notifcationBody.put("des_loc", notificationData.desLoc)
        notifcationBody.put("amount", notificationData.amount)
        notifcationBody.put("device_id", notificationData.deviceId)


        notification.put("registration_ids", JSONArray(array))
        notification.put("data", notifcationBody)

        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
                //msg.setText("")
            },
            Response.ErrorListener {
                context.toast("Request error")
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }


    fun getUserToken(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        Log.e("BARRER_TOKEN", "Bearer ${(sp.getString(App.preferenceUserToken, "").toString())}")
        return "Bearer ${(sp.getString(App.preferenceUserToken, "").toString())}"
    }

    fun getUserID(context: Context): String {
        val sp = context.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        return sp.getString(App.preferenceUserId, "")!!
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


    fun openDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // add a button
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->

        })

        val dialog = builder.create()
        dialog.show()
    }


    fun setNoteText(partialPay: String, amountPayToDriver: String): String {

        val note =
            "Note : You need to pay $RS$partialPay at the time of booking and  $RS$amountPayToDriver to driver by cash or in other format as per your choice after end of the journey. If no driver get assigned after booking then the amount will refund within 2 working days to your bank account"
        return note
    }

    fun bitmapDescriptorFromVector(vectorResId: Int, context: Context): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun calculateTime(location1: Location, location2: Location) : String {
        val distanceInMeters = location1.distanceTo(location2)
        //For example spead is 10 meters per minute.
        val speedIs10MetersPerMinute = 583
        val estimatedDriveTimeInMinutes: Float = distanceInMeters / speedIs10MetersPerMinute
        val time = String.format("%.0f", estimatedDriveTimeInMinutes)
        return time
    }

}