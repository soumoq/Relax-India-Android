package org.relaxindia.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.nav_header.view.*
import org.relaxindia.R
import org.relaxindia.viewModel.ApiCallViewModel

class MyProfileActivity : AppCompatActivity() {

    //view-model
    lateinit var apiCallViewModel: ApiCallViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()
        apiCallViewModel.profileInfo(this)

    }


    private fun observeViewModel() {

        apiCallViewModel.profileInfo.observe(this, Observer {
            profile_phone.setText(it.data.phone)
            profile_name.setText(it.data.name)
            profile_email.setText(it.data.email)
            profile_address.setText(it.data.address)
            profile_pin_code.setText(it.data.pincode)
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