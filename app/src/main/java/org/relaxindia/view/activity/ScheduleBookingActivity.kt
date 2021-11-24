package org.relaxindia.view.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_schedule_booking.*
import kotlinx.android.synthetic.main.sheet_booking_list.*
import org.json.JSONObject
import org.relaxindia.R
import org.relaxindia.SuccessScheduleReq
import org.relaxindia.model.ScheduleReq
import org.relaxindia.service.VollyApi
import org.relaxindia.util.App
import org.relaxindia.util.toast
import org.relaxindia.view.recyclerView.ScheduleBookingAdapter
import org.relaxindia.view.recyclerView.SuccessScheduleBookingAdapter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleBookingActivity : AppCompatActivity(), PaymentResultListener {

    var fromAddress: String = ""
    var toAddress: String = ""

    //updateBookinf
    var bookingId: String = ""
    var amount: String = ""

    //calender
    val myCalendar = Calendar.getInstance()

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

        schedule_booking_back.setOnClickListener {
            onBackPressed()
        }

        //Payment check out
        Checkout.preload(applicationContext)

        //FROM LOCATION
        val autocompleteFragmentFrom =
            supportFragmentManager.findFragmentById(R.id.from_location_fragment) as AutocompleteSupportFragment?
        autocompleteFragmentFrom!!.setPlaceFields(
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        )
        autocompleteFragmentFrom.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                fromAddress = place.address.toString()
            }

            override fun onError(status: Status) {
                Log.e("SEARCH_ERROR", status.toString())
            }
        })


        //TO LOCATION
        val autocompleteFragmentTo =
            supportFragmentManager.findFragmentById(R.id.to_location_fragment) as AutocompleteSupportFragment?
        autocompleteFragmentTo!!.setPlaceFields(
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        )
        autocompleteFragmentTo.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                toAddress = place.address.toString()
            }

            override fun onError(status: Status) {
                Log.e("SEARCH_ERROR", status.toString())
            }
        })


        //PICK DATE
        val selectDateDialog =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                selectDate()
            }
        select_date_et.setOnClickListener {
            val datePicker = DatePickerDialog(
                this, selectDateDialog, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() + (1000 * 3600 * 24)
            datePicker.show()
        }


        start_schedule_booking.setOnClickListener {
            if (fromAddress != "" && toAddress != "" &&
                select_date_et.text.toString().isNotEmpty() &&
                schedule_time.text.toString().isNotEmpty()
            ) {
                VollyApi.scheduleBookingReq(
                    this, fromAddress, toAddress,
                    "${select_date_et.text.toString()} ${
                        schedule_time.text.toString().isNotEmpty()
                    }",
                    user_comment.text.toString()
                )
            } else {
                toast("Invalid input!!")
            }
        }

        schedule_req.setOnClickListener {
            VollyApi.getAllScheduleReq(this)
        }

        get_schedule_req.setOnClickListener {
            VollyApi.getScheduleBooking(this)
        }


    }

    fun reloadActivity() {
        finish();
        startActivity(intent);
    }

    private fun selectDate() {
        val myFormat = "dd-MM-yy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        select_date_et.setText(sdf.format(myCalendar.time))
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


    fun startPayment(payableAmount: Double, bookingId: String) {
        this.bookingId = bookingId;
        this.amount = payableAmount.toString()
        val checkout = Checkout()
        checkout.setKeyID(App.paymentkeyId)

        checkout.setImage(R.drawable.logo)

        val activity: Activity = this

        try {
            val options = JSONObject()
            options.put("name", App.getUserName(this))
            options.put("description", "Reference No. #123456")
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            //options.put("order_id", "order_DBJOWzybf0sJbb") //from response of step 3.
            options.put("theme.color", "#3399cc")
            options.put("currency", "INR")
            options.put("amount", "${payableAmount * 100}") //500 * 100
            options.put("prefill.email", App.getUserEmail(this))
            options.put("prefill.contact", App.getUserPhone(this))
            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)
            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e("BookNowActivity_payment", "Error in starting Razorpay Checkout", e)
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        toast("Payment successful.")
        if (bookingId != "") {
            Log.e("BOOKING_INFO_UP", "$bookingId $p0 $amount")
            bookingListSheet.dismiss()
            VollyApi.updateScheduleBooking(this, bookingId, p0!!, amount)
        } else {
            toast("Error : Booking id not found")
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        toast("Something went wrong with payment : $p1")
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