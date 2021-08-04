package org.relaxindia.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_otp.*
import org.relaxindia.R
import java.util.concurrent.TimeUnit


class OtpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val phoneNumber: String = intent.getStringExtra("phone_number").toString()
        val text = "Don't receive SMS?  <font color=#1b9ff1>Resend OTP</font>"
        resend_sms.text = Html.fromHtml(text)
        otp_phone_number_display.text = "Enter the 6-digit code send to \n$phoneNumber"

        otp_back.setOnClickListener {
            onBackPressed()
        }


        otp_proceed.button.setOnClickListener {
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