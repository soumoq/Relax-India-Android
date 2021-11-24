package org.relaxindia.service

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import org.relaxindia.SuccessScheduleReq
import org.relaxindia.model.ScheduleReq
import org.relaxindia.service.location.GpsTracker
import org.relaxindia.util.App
import org.relaxindia.util.toast
import org.relaxindia.view.activity.BookNowActivity
import org.relaxindia.view.activity.BookingSuccessfulActivity
import org.relaxindia.view.activity.HomeActivity
import org.relaxindia.view.activity.ScheduleBookingActivity

object VollyApi {
    fun updateDeviceToken(context: Context, deviceToken: String) {

        val URL = "${App.apiBaseUrl}${App.UPDATE_DEVICE_TOKEN}"
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.PATCH, URL,
                Response.Listener<String?> { response ->
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            //context.toast("Device token update successful")
                            if (deviceToken == "")
                                (context as HomeActivity).logout()
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", e.message!!)
                    }
                },
                Response.ErrorListener { error ->
                    context.toast("Something went wrong: $error")
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    return header
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["device_token"] = deviceToken
                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    fun findAmbulance(context: Context, range: String) {
        val gpsTracker = GpsTracker(context)
        context.toast("Please wait... Searching Ambulance")
        val URL = "${App.apiBaseUrl}${App.FIND_AMBULANCE}"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.POST, URL,
                Response.Listener<String?> { response ->
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            val driverArr = jsonObj.getJSONArray("data")
                            //context.toast("Device token update successful")
                            (context as BookNowActivity).driverFindStatus(driverArr.length())

                        } else {
                            App.ambulanceSearchRedis = "10"
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", e.message!!)
                    }
                },
                Response.ErrorListener { error ->
                    context.toast("Something went wrong: $error")
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    return header
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    Log.e("FIND_LAT_LON", "${gpsTracker.latitude} : ${gpsTracker.longitude}")
                    params["radius"] = range
                    params["user_latitude"] = "${gpsTracker.latitude}"
                    params["user_longitude"] = "${gpsTracker.longitude}"

                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    fun cancelBooking(context: Context, bookingId: String) {
        context.toast("Please wait...")
        val URL = "${App.apiBaseUrl}${App.CANCEL_BOOKING}"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.PATCH, URL,
                Response.Listener<String?> { response ->
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (!error) {
                            context.toast("Ambulance cancel successful.")
                            (context as BookingSuccessfulActivity).gotoHomeActivity()

                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", e.message!!)
                    }
                },
                Response.ErrorListener { error ->
                    context.toast("Something went wrong: $error")
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    return header
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["booking_id"] = bookingId

                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)

    }

    fun scheduleBookingReq(
        context: Context,
        from: String,
        to: String,
        dateTime: String,
        comment: String
    ) {
        context.toast("Please wait...")
        val URL = "${App.apiBaseUrl}${App.SCHEDULE_REQUEST}"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.POST, URL,
                Response.Listener<String?> { response ->
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (error) {
                            context.toast("Something went wrong!!!")
                        } else {
                            context.toast("Booking request successful...")
                            (context as ScheduleBookingActivity).reloadActivity()
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", e.message!!)
                    }
                },
                Response.ErrorListener { error ->
                    context.toast("Something went wrong: $error")
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    return header
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["from_location"] = from
                    params["to_location"] = to
                    params["schedule_date_time"] = dateTime
                    params["user_comment"] = comment

                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)

    }

    fun getAllScheduleReq(context: Context) {
        context.toast("Please wait...")
        val URL = "${App.apiBaseUrl}${App.ALL_SCHEDULE_REQ}"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.POST, URL,
                Response.Listener<String?> { response ->
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (error) {
                            context.toast("Something went wrong!!!")
                        } else {
                            val jsonArr = jsonObj.getJSONArray("data")
                            if (jsonArr.length() > 0) {
                                val objList = ArrayList<ScheduleReq>()
                                for (i in 0 until jsonArr.length()) {
                                    val obj = jsonArr.getJSONObject(i)
                                    objList.add(
                                        ScheduleReq(
                                            obj.getInt("booking_id"),
                                            obj.getDouble("booking_amount"),
                                            obj.getDouble("total_amount"),
                                            obj.getString("from_location"),
                                            obj.getString("to_location"),
                                            obj.getString("schedule_date_time"),
                                            obj.getString("user_comment"),
                                            obj.getString("status")
                                        )
                                    )
                                }
                                (context as ScheduleBookingActivity).setScheduleBookingList(objList)
                            } else {
                                context.toast("Schedule Booking is not Requested Yet...")
                            }
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", e.message!!)
                    }
                },
                Response.ErrorListener { error ->
                    context.toast("Something went wrong: $error")
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    return header
                }

            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    fun updateScheduleBooking(
        context: Context,
        bookingId: String,
        txId: String,
        amount: String
    ) {
        context.toast("Please wait... Updating payment")
        val URL = "${App.apiBaseUrl}${App.UPDATE_SCHEDULE_REQ}"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.PATCH, URL,
                Response.Listener<String?> { response ->
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (error) {
                            context.toast("Something went wrong!!!")
                        } else {
                            context.toast("Booking successfully done!!!")
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", e.message!!)
                    }
                },
                Response.ErrorListener { error ->
                    context.toast("Something went wrong: $error")
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    return header
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["booking_id"] = bookingId
                    params["tx_id"] = txId
                    params["payable_amount"] = amount

                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

    fun getScheduleBooking(context: Context) {
        context.toast("Please wait...")
        val URL = "${App.apiBaseUrl}${App.GET_SCHEDULE_BOOKING}"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.POST, URL,
                Response.Listener<String?> { response ->
                    try {
                        val jsonObj = JSONObject(response)
                        val error = jsonObj.getBoolean("error")
                        if (error) {
                            context.toast("Something went wrong!!!")
                        } else {
                            val jsonArr = jsonObj.getJSONArray("data")
                            if (jsonArr.length() > 0) {
                                val objList = ArrayList<SuccessScheduleReq>()
                                for (i in 0 until jsonArr.length()) {
                                    val obj = jsonArr.getJSONObject(i)
                                    objList.add(
                                        SuccessScheduleReq(
                                            obj.getInt("driver_id"),
                                            obj.getString("driver_name"),
                                            obj.getString("driver_phone"),
                                            obj.getString("driver_image"),
                                            obj.getString("driver_secondary_phone"),
                                            obj.getDouble("driver_avg_rating"),
                                            obj.getString("from_location"),
                                            obj.getString("to_location"),
                                            obj.getString("schedule_date_time"),
                                            obj.getString("user_comment"),
                                            obj.getDouble("booking_amount"),
                                            obj.getDouble("total_amount"),
                                            obj.getString("status"),
                                            obj.getString("date"),
                                        )

                                    )
                                }
                                (context as ScheduleBookingActivity).setGetScheduleBookingList(
                                    objList
                                )
                            } else {
                                context.toast("Schedule Booking is not Requested Yet...")
                            }
                        }
                    } catch (e: JSONException) {
                        App.openDialog(context, "Error", e.message!!)
                    }
                },
                Response.ErrorListener { error ->
                    context.toast("Something went wrong: $error")
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["Authorization"] = App.getUserToken(context)
                    return header
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

}