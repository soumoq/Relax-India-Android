package org.relaxindia.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import kotlinx.android.synthetic.main.activity_otp.*
import org.relaxindia.R
import org.relaxindia.util.toast

class OtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val text =  "Don't receive SMS?  <font color=#1b9ff1>Resend OTP</font>"
        resend_sms.text = Html.fromHtml(text)

        otp_back.setOnClickListener {
            onBackPressed()
        }

        otp_proceed.button.setOnClickListener {
            toast(pin_view.text.toString())
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