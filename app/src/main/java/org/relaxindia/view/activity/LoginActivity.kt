package org.relaxindia.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_login.*
import org.relaxindia.R
import org.relaxindia.util.App
import org.relaxindia.util.toast
import org.relaxindia.viewModel.ApiCallViewModel

class LoginActivity : AppCompatActivity() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    lateinit var apiCallViewModel: ApiCallViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()
        val text =
            "By Continuing, you agree to the <font color=#1b9ff1>Terms Of Service </font>and <font color=#1b9ff1>Privacy Policy</font>"
        terms_service.text = Html.fromHtml(text)

        login_continue.button.setOnClickListener {

            if (login_phone_number.text.toString().length == 10) {
//                val intent = Intent(this, OtpActivity::class.java)
//                intent.putExtra("phone_number", login_phone_number.text.toString())
//                startActivity(intent)
                apiCallViewModel.loginInfo(this,login_phone_number.text.toString())
            } else {
                login_phone_number.error = "Enter valid phone number"
            }

        }
    }

    private fun observeViewModel(){
        apiCallViewModel.loginInfo.observe(this, Observer {
            if (!it.error){
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra("phone_number", login_phone_number.text.toString())
                intent.putExtra("otp", it.data.otp.toString())
                startActivity(intent)
            }else{
                toast("Something went wrong")
            }
        })
    }


    override fun onResume() {
        super.onResume()
        if (App.isLocationEnabled(this)) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        } else {
            App.openLocationDialog(this, "Enable Location", App.locationAlert)
        }
    }


    override fun onStart() {
        super.onStart()

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