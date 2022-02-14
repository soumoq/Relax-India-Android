package org.relaxindia.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_otp.*
import org.relaxindia.R
import org.relaxindia.util.App
import org.relaxindia.util.toast
import org.relaxindia.viewModel.ApiCallViewModel
import java.util.concurrent.TimeUnit


class OtpActivity : AppCompatActivity() {

    lateinit var apiCallViewModel: ApiCallViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()


        val phoneNumber: String = intent.getStringExtra("phone_number").toString()
        val text = "Don't receive SMS?  <font color=#1b9ff1>Resend OTP</font>"
        resend_sms.text = Html.fromHtml(text)
        otp_phone_number_display.text = "Enter the 6-digit code send to \n$phoneNumber"

        otp_back.setOnClickListener {
            onBackPressed()
        }

        resend_sms.setOnClickListener {
            apiCallViewModel.loginInfo(this, phoneNumber)
        }


        otp_proceed.button.setOnClickListener {
            apiCallViewModel.otpInfo(
                this,
                intent.getStringExtra("phone_number")!!,
                pin_view.text.toString()
            )
        }


    }

    private fun observeViewModel() {
        apiCallViewModel.otpInfo.observe(this, Observer {
            if (!it.error) {
                val sp = getSharedPreferences("user_info", Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putString(App.preferenceUserToken, it.data.access_token)
                editor.commit()

                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            } else {
                App.openDialog(this, "Error: ", it.message)
            }
        })

        apiCallViewModel.loginInfo.observe(this, Observer {
            if (!it.error) {
                toast("Otp Sent successful")
            } else {
                toast("Something went wrong")
            }
        })

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