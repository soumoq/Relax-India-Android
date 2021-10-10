package org.relaxindia.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.Checkout
import kotlinx.android.synthetic.main.activity_book_now.*
import org.json.JSONObject

import android.app.Activity
import android.location.Location

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.razorpay.PaymentResultListener
import org.json.JSONArray
import org.relaxindia.R
import org.relaxindia.service.location.GpsTracker
import org.relaxindia.util.App
import org.relaxindia.view.recyclerView.DefaultServiceAdapter
import org.relaxindia.view.recyclerView.OptionalServiceAdapter
import org.relaxindia.viewModel.ApiCallViewModel
import java.lang.Exception


class BookNowActivity : AppCompatActivity(), PaymentResultListener {

    //view-model
    private lateinit var apiCallViewModel: ApiCallViewModel

    //Create Json
    private val serviceJson = JSONObject()
    private val arr = JSONArray()
    private val serviceIdList = ArrayList<Int>()

    //Booking details
    private var payableAmount = 0.0
    private var totalAmount = 0.0;

    //location val
    var sourceLoc = ""
    var desLoc = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_now)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        apiCallViewModel.serviceInfo(this, "Optional Service")
        apiCallViewModel.serviceInfo(this, "Default Service")
        observeViewModel()

        serviceIdList.add(intent.getStringExtra("service_id")?.toInt()!!)
        for (i in 0 until serviceIdList.size) {
            arr.put(serviceIdList[i])
        }
        serviceJson.put("service", arr)
        //Log.e("CONVAERJKJDAJ", serviceJson.toString())
        apiCallViewModel.selectedServiceInfo(this, serviceJson.toString())

        //Payment check out
        Checkout.preload(applicationContext)

        //Calculate distance
        val startPoint = Location("SOURCE")
        startPoint.latitude = "${intent.getStringExtra("sourceLat")}".toDouble()
        startPoint.longitude = "${intent.getStringExtra("sourceLon")}".toDouble()
        val endPoint = Location("DES")
        endPoint.latitude = "${intent.getStringExtra("desLat")}".toDouble()
        endPoint.longitude = "${intent.getStringExtra("desLon")}".toDouble()
        val distance = startPoint.distanceTo(endPoint) / 1000
        book_now_destance.text = "$distance K.M."

        sourceLoc = intent.getStringExtra("source_loc")!!
        desLoc = intent.getStringExtra("des_loc")!!

        des_location.text = desLoc
        source_location.text = sourceLoc



        pay_to_book.setOnClickListener {
            startPayment()
        }

        book_now_back.setOnClickListener {
            onBackPressed()
        }

    }


    private fun observeViewModel() {
        apiCallViewModel.getService.observe(this, Observer {
            if (!it.error) {
                if (it.message == "Optional Service") {
                    val serviceAdapter = OptionalServiceAdapter(this)
                    op_service_recycler_view.adapter = serviceAdapter
                    serviceAdapter.updateData(it.data)
                } else if (it.message == "Default Service") {
                    val defaultServiceAdapter = DefaultServiceAdapter(this)
                    default_service.layoutManager = GridLayoutManager(this, 3)
                    default_service.adapter = defaultServiceAdapter
                    defaultServiceAdapter.updateData(it.data)
                }
            }
        })

        apiCallViewModel.getSelectedService.observe(this, Observer {
            if (!it.error) {
                base_price.text = "${App.rs}${(it.data.payable_amount + it.data.rest_amount)}"
                book_now_amount.text = "Pay now ${App.rs}${it.data.payable_amount}"
                payableAmount = it.data.payable_amount
                totalAmount = it.data.payable_amount + it.data.rest_amount
                partial_pay.text = "${App.rs}${it.data.payable_amount}"
                payto_driver.text = "${App.rs}${it.data.rest_amount}"
                note_text.text = App.setNoteText(it.data.rest_amount.toString())
            }
        })

        apiCallViewModel.getSaveService.observe(this, Observer {
            if (!it.error) {
                val intent = Intent(this, BookingSuccessfulActivity::class.java)
                intent.putExtra("booking_id", it.data.booking_id)
                intent.putExtra("source_loc", sourceLoc)
                intent.putExtra("des_loc", desLoc)
                intent.putExtra("amount", payto_driver.text.toString())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })


    }

    fun updatePrice(select: Boolean, serviceId: Int) {
        if (select) {
            serviceIdList.add(serviceId)
        } else {
            serviceIdList.remove(serviceId)
        }
        for (i in 0 until arr.length()) {
            arr.remove(i)
        }
        for (i in 0 until serviceIdList.size) {
            arr.put(serviceIdList[i])
        }
        serviceJson.put("service", arr)
        //Log.e("CONVAERJKJDAJ", serviceJson.toString())
        apiCallViewModel.selectedServiceInfo(this, serviceJson.toString())
        observeViewModel()

    }


    private fun startPayment() {
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
        val bookingDetails = JSONObject()

        val gpsThread = GpsTracker(this)
        bookingDetails.put("booking_amount", payableAmount)
        bookingDetails.put("total_amount", totalAmount)
        bookingDetails.put("payable_amount", payableAmount)
        bookingDetails.put("wallet_amount", "0.00")
        bookingDetails.put("from_location", sourceLoc)
        bookingDetails.put("to_location", desLoc)
        bookingDetails.put("tx_id", p0.toString())
        bookingDetails.put("service", arr)
        bookingDetails.put("user_latitude", gpsThread.latitude.toString())
        bookingDetails.put("user_longitude", gpsThread.longitude.toString())
        Log.e("JSONRES", bookingDetails.toString())
        apiCallViewModel.saveServiceInfo(this, bookingDetails.toString())
        observeViewModel()

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.e("PAYMRNT_INFO", p1.toString())
        App.openDialog(this, "Error", "Something went wrong with payment \n $p1")
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