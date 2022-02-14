package org.relaxindia.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.nav_header.view.*
import org.relaxindia.R
import org.relaxindia.service.VollyApi
import org.relaxindia.util.App
import org.relaxindia.util.toast
import org.relaxindia.viewModel.ApiCallViewModel

class MyProfileActivity : AppCompatActivity() {

    //view-model
    lateinit var apiCallViewModel: ApiCallViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        profile_back.setOnClickListener {
            onBackPressed()
        }

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()
        apiCallViewModel.profileInfo(this)

        update_profile.setOnClickListener {

            VollyApi.updateProfile(
                this, profile_name.text.toString(),
                profile_email.text.toString(),
                profile_address.text.toString(),
                profile_pin_code.text.toString()
            )

//            if (profile_name.text.toString().equals("") ||
//                profile_email.text.toString().equals("") ||
//                profile_address.text.toString().equals("") ||
//                profile_pin_code.text.toString().equals("")
//            ) {
//                toast("Enter valid input")
//            } else {
//                apiCallViewModel.updateProfileInfo(
//                    this,
//                    profile_name.text.toString(),
//                    profile_email.text.toString(),
//                    profile_address.text.toString(),
//                    profile_pin_code.text.toString()
//                )
//            }
        }

    }


    private fun observeViewModel() {
        apiCallViewModel.profileInfo.observe(this, Observer {
            profile_phone.setText(it.data.phone)
            profile_name.setText(it.data.name)
            profile_email.setText(it.data.email)
            profile_address.setText(it.data.address)
            profile_pin_code.setText(it.data.pincode)
        })

        apiCallViewModel.updateProfile.observe(this, Observer {
            if (it.error) {
                App.openDialog(this, "Error", "Something went wrong")
            } else {
                toast(it.message)
                onBackPressed()
            }
        })

    }

    fun profileUpdated() {
        onBackPressed()
    }


    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        if (profile_name.text.toString().equals("")) {
            App.openDialog(this, "Alert", "Please update your name.")
        } else {
            super.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }


}