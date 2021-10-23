package org.relaxindia.service

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import org.relaxindia.util.App
import org.relaxindia.util.toast
import org.relaxindia.view.activity.BookNowActivity
import org.relaxindia.view.activity.HomeActivity

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

    fun findAmbulance(context: Context, range : String) {
        context.toast("Please wait...")
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
                    params["radius"] = range
                    params["user_latitude"] = "22.85"
                    params["user_longitude"] = "88.02"

                    return params
                }
            }
        requestQueue.cache.clear()
        requestQueue.add(stringRequest)
    }

}