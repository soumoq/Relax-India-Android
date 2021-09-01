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
import com.razorpay.PaymentResultListener
import org.json.JSONArray
import org.relaxindia.R
import org.relaxindia.util.App
import org.relaxindia.util.toast
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

    private var servicePrice = 0.0

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

        des_location.text = intent.getStringExtra("des_loc")
        source_location.text = intent.getStringExtra("source_loc")



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
                    default_service.adapter = defaultServiceAdapter
                    defaultServiceAdapter.updateData(it.data)
                }
            }
        })

        apiCallViewModel.getSelectedService.observe(this, Observer {
            if (!it.error) {
                base_price.text = "${App.rs}${(it.data.payable_amount + it.data.rest_amount)}"
                book_now_amount.text = "Pay now ${App.rs}${it.data.payable_amount}"
                servicePrice = it.data.payable_amount
                partial_pay.text = "${App.rs}${it.data.payable_amount}"
                payto_driver.text = "${App.rs}${it.data.rest_amount}"
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
            options.put("amount", "${servicePrice * 100}") //500 * 100
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
        Log.e("PAYMRNT_INFO", p0.toString())
        val intent = Intent(this, BookingSuccessfulActivity::class.java)
        startActivity(intent)
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