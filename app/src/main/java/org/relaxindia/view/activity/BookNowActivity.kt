package org.relaxindia.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.Checkout
import kotlinx.android.synthetic.main.activity_book_now.*
import org.json.JSONObject

import android.app.Activity

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.sheet_home_dashboard.*
import org.relaxindia.R
import org.relaxindia.util.App
import org.relaxindia.view.recyclerView.OptionalServiceAdapter
import org.relaxindia.view.recyclerView.ServiceAdapter
import org.relaxindia.viewModel.ApiCallViewModel
import java.lang.Exception


class BookNowActivity : AppCompatActivity(), PaymentResultListener {

    //view-model
    lateinit var apiCallViewModel: ApiCallViewModel

    var servicePrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_now)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()
        apiCallViewModel.serviceInfo(this, "Optional Service")

        //Payment check out
        Checkout.preload(applicationContext)

        des_location.text = intent.getStringExtra("des_loc")
        source_location.text = intent.getStringExtra("source_loc")
        servicePrice = (((intent.getStringExtra("service_price"))?.toDouble())!! / 100).toInt()
        total_amount.text = servicePrice.toString()

        book_now_amount.text =
            "${App.rs}$servicePrice"
        base_price.text =
            "${App.rs}$servicePrice"

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
                val serviceAdapter = OptionalServiceAdapter(this)
                op_service_recycler_view.adapter = serviceAdapter
                serviceAdapter.updateData(it.data)
            }
        })
    }

    fun updatePrice(select: Boolean, amount: Int) {
        var tempAmount: Int = 0
        var tempServiceAmount: Int = 0

        if (select) {
            tempAmount = optional_service.text.toString().toInt() + amount
            tempServiceAmount = total_amount.text.toString().toInt() + amount
        } else {
            tempAmount = optional_service.text.toString().toInt() - amount
            tempServiceAmount = total_amount.text.toString().toInt() - amount
        }
        optional_service.text = tempAmount.toString()
        total_amount.text = tempServiceAmount.toString()
        book_now_amount.text = "${App.rs}${tempServiceAmount.toString()}"
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
            options.put("amount", "${total_amount.text.toString().toDouble() * 100}") //500 * 100
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