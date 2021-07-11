package org.relaxindia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val text =  "By Continuing, you agree to the <font color=#1b9ff1>Terms Of Service </font>and <font color=#1b9ff1>Privacy Policy</font>"
        terms_service.text = Html.fromHtml(text)

        login_continue.setOnClickListener {

        }

    }
}