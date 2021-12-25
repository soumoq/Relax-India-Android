package org.relaxindia.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_schedule_booking.*
import kotlinx.android.synthetic.main.activity_schedule_booking.get_schedule_req
import kotlinx.android.synthetic.main.activity_schedule_booking.schedule_req
import kotlinx.android.synthetic.main.activity_schedule_booking.start_schedule_booking
import kotlinx.android.synthetic.main.activity_start_schedule_booking.*
import kotlinx.android.synthetic.main.sheet_booking_list.*
import org.relaxindia.R
import org.relaxindia.SuccessScheduleReq
import org.relaxindia.model.ScheduleReq
import org.relaxindia.service.VollyApi
import org.relaxindia.view.recyclerView.ScheduleBookingAdapter
import org.relaxindia.view.recyclerView.SuccessScheduleBookingAdapter

class ScheduleBookingActivity : AppCompatActivity() {

    //Button sheet
    lateinit var bookingListSheet: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_booking)

        bookingListSheet = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bookingListSheet.setContentView(R.layout.sheet_booking_list)
        bookingListSheet.back_sheet_schedule.setOnClickListener {
            bookingListSheet.dismiss()
        }

        start_schedule_booking.setOnClickListener {
            val intent = Intent(this, StartScheduleBookingActivity::class.java)
            startActivity(intent)

        }

        schedule_req.setOnClickListener {
            VollyApi.getAllScheduleReq(this)
        }

        get_schedule_req.setOnClickListener {
            VollyApi.getScheduleBooking(this)
        }

    }

    fun setScheduleBookingList(bookingList: ArrayList<ScheduleReq>) {
        //toast(bookingList.size.toString())
        bookingListSheet.show()
        val scheduleAdapter = ScheduleBookingAdapter(this)
        bookingListSheet.schedule_booking_list.adapter = scheduleAdapter
        scheduleAdapter.updateData(bookingList)
    }

    fun setGetScheduleBookingList(successScheduleList: ArrayList<SuccessScheduleReq>) {
        bookingListSheet.show()
        val successScheduleAdapter = SuccessScheduleBookingAdapter(this)
        bookingListSheet.schedule_booking_list.adapter = successScheduleAdapter
        successScheduleAdapter.updateData(successScheduleList)
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