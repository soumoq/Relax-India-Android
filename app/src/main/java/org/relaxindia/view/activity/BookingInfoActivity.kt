package org.relaxindia.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_booking_info.*
import org.json.JSONArray
import org.json.JSONObject
import org.relaxindia.R
import org.relaxindia.util.loadImage

class BookingInfoActivity : AppCompatActivity() {

    //Intent date
    private var fromLocation = ""
    private var toLocation = ""
    private var driverName = ""
    private var serviceDetails = ""
    private var driverImage = ""
    private var date = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_info)

        booking_info_back.setOnClickListener {
            onBackPressed()
        }

        fromLocation = intent.getStringExtra("from_location")!!
        toLocation = intent.getStringExtra("to_location")!!
        driverName = intent.getStringExtra("driver_name")!!
        serviceDetails = intent.getStringExtra("service_details")!!
        driverImage = intent.getStringExtra("driver_image")!!
        date = intent.getStringExtra("date")!!

        booking_info_from_loc.text = fromLocation
        booking_info_des_loc.text = toLocation
        booking_info_driver_name.text = driverName
        booking_info_driver_image.loadImage(driverImage)
        booking_info_date.text = date

        val jsonObj = JSONArray(serviceDetails)
        Log.e("JSONDATA", jsonObj.toString())

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