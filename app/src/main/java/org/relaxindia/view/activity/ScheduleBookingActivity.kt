package org.relaxindia.view.activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_schedule_booking.*
import org.relaxindia.R
import org.relaxindia.util.toast
import java.text.SimpleDateFormat
import java.util.*

class ScheduleBookingActivity : AppCompatActivity() {

    var fromAddress: String = ""
    var toAddress: String = ""

    //calender
    val myCalendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_booking)

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
                toast("$fromAddress $toAddress ${select_date_et.text.toString()} ${schedule_time.text.toString()} ${user_comment.text.toString()}")
            }else{
                toast("Invalid input!!")
            }
        }


    }

    private fun selectDate() {
        val myFormat = "dd-MM-yy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        select_date_et.setText(sdf.format(myCalendar.time))
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