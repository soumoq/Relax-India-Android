package org.relaxindia.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_booking_info.*
import org.json.JSONArray
import org.relaxindia.R
import org.relaxindia.util.App
import org.relaxindia.util.loadImage
import org.relaxindia.view.recyclerView.OtherServiceAdapter
import androidx.core.content.ContextCompat.startActivity


class BookingInfoActivity : AppCompatActivity() {

    //Intent date
    private var fromLocation = ""
    private var toLocation = ""
    private var driverName = ""
    private var serviceDetails = ""
    private var driverImage = ""
    private var date = ""
    private var bookingAmount = ""
    private var totalAmount = ""
    private var fromLatitude = ""
    private var fromLongitude = ""
    private var toLatitude = ""
    private var toLongitude = ""
    private var driverId = ""
    private var driverPhone = ""


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
        bookingAmount = intent.getStringExtra("booking_amount")!!
        totalAmount = intent.getStringExtra("total_amount")!!
        fromLatitude = intent.getStringExtra("from_latitude")!!
        fromLongitude = intent.getStringExtra("from_longitude")!!
        toLatitude = intent.getStringExtra("to_latitude")!!
        toLongitude = intent.getStringExtra("to_longitude")!!
        driverId = intent.getStringExtra("driver_id")!!
        driverPhone = intent.getStringExtra("driver_phone")!!


        booking_info_phone.text = "Call: $driverPhone"
        booking_info_from_loc.text = fromLocation
        booking_info_des_loc.text = toLocation
        booking_info_driver_name.text = driverName
        booking_info_driver_image.loadImage(driverImage)
        booking_info_date.text = date
        booking_info_booking_amt.text = "${App.rs}$bookingAmount"
        booking_info_total_amt.text = "${App.rs}$totalAmount"
        booking_info_driver_amt.text =
            "${App.rs}${totalAmount.toDouble() - bookingAmount.toDouble()}"

        val jsonArr = JSONArray(serviceDetails)
        Log.e("JSONDATA", jsonArr.getJSONObject(0).getString("name"))
        val otherServiceAdapter = OtherServiceAdapter(this)
        other_service_list.adapter = otherServiceAdapter
        otherServiceAdapter.updateData(jsonArr)

        booking_info_phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "$driverPhone"))
            startActivity(intent)
        }

        track_booking.setOnClickListener {
            val intent = Intent(this, TrackActivity::class.java)
            intent.putExtra("from_latitude", fromLatitude)
            intent.putExtra("from_longitude", fromLongitude)
            intent.putExtra("to_latitude", toLatitude)
            intent.putExtra("to_longitude", toLongitude)
            intent.putExtra("driver_id", driverId)
            intent.putExtra("driver_name", driverName)
            intent.putExtra("driver_image", driverImage)
            intent.putExtra("from_location", fromLocation)
            intent.putExtra("to_location", toLocation)
            intent.putExtra("driver_phone", driverPhone)
            startActivity(intent)
        }

        booking_info_customer_support.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "9874408080"))
            startActivity(intent)
        }

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