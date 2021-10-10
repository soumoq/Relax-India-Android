package org.relaxindia.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_booking_info.*
import org.json.JSONArray
import org.relaxindia.R
import org.relaxindia.util.App
import org.relaxindia.util.loadImage
import org.relaxindia.view.recyclerView.OtherServiceAdapter

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

        booking_info_from_loc.text = fromLocation
        booking_info_des_loc.text = toLocation
        booking_info_driver_name.text = driverName
        booking_info_driver_image.loadImage(driverImage)
        booking_info_date.text = date
        booking_info_booking_amt.text = "${App.rs}$bookingAmount"
        booking_info_total_amt.text = "${App.rs}$totalAmount"
        booking_info_driver_amt.text = "${App.rs}${totalAmount.toDouble() - bookingAmount.toDouble()}"

        val jsonArr = JSONArray(serviceDetails)
        Log.e("JSONDATA",jsonArr.getJSONObject(0).getString("name"))
        val otherServiceAdapter = OtherServiceAdapter(this)
        other_service_list.adapter = otherServiceAdapter
        otherServiceAdapter.updateData(jsonArr)

        track_booking.setOnClickListener {
            val intent = Intent(this,TrackActivity::class.java)
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