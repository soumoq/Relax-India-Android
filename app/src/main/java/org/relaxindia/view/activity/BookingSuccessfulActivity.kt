package org.relaxindia.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import org.relaxindia.R
import org.relaxindia.model.NotificationDataModel
import org.relaxindia.util.App
import org.relaxindia.viewModel.ApiCallViewModel
import android.os.CountDownTimer
import android.view.View
import kotlinx.android.synthetic.main.activity_bookig_successful.*
import org.relaxindia.service.VollyApi


class BookingSuccessfulActivity : AppCompatActivity() {

    //view-model
    private lateinit var apiCallViewModel: ApiCallViewModel

    //Local var
    private var bookingId = ""
    private var sourceLoc = ""
    private var desLoc = ""
    private var amount = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookig_successful)

        bookingId = intent.getStringExtra("booking_id")!!
        sourceLoc = intent.getStringExtra("source_loc")!!
        desLoc = intent.getStringExtra("des_loc")!!
        amount = intent.getStringExtra("amount")!!

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        apiCallViewModel.getDriverListInfo(this)
        observeViewModel()


        val cT: CountDownTimer = object : CountDownTimer(100000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val v = String.format("%02d", millisUntilFinished / 60000)
                val va = (millisUntilFinished % 120000 / 1000).toInt()
                back_time.text = v + ":" + String.format("%02d", va)
            }

            override fun onFinish() {
                cancel_booking_view.visibility = View.GONE
            }
        }
        cT.start()

        cancel_booking.setOnClickListener {
            VollyApi.cancelBooking(this,bookingId)
        }

        go_to_home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }


    private fun observeViewModel() {
        apiCallViewModel.getDriverList.observe(this, Observer {
            if (!it.error) {
                val deviceIdArr = ArrayList<String>()
                for (i in it.data.indices) {
                    if (it.data[i].device_token != null) {
                        Log.e("CHECK_NULL", "device - ${it.data[i].device_token}")
                        deviceIdArr.add(it.data[i].device_token)
                    }
                }
                Log.e("CHECK_LOG", "$bookingId\n$sourceLoc\n$desLoc\n$amount")
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
//                    App.sendNotification(
//                        this,
//                        deviceIdArr,
//                        NotificationDataModel(bookingId, sourceLoc, desLoc, amount, it)
//                    )
                }
            }
        })
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}