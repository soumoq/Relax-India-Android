package org.relaxindia.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.relaxindia.R
import org.relaxindia.util.App
import org.relaxindia.viewModel.ApiCallViewModel

class BookingSuccessfulActivity : AppCompatActivity() {

    //view-model
    private lateinit var apiCallViewModel: ApiCallViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookig_successful)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        apiCallViewModel.getDriverListInfo(this)
        observeViewModel()
    }


    private fun observeViewModel() {
        apiCallViewModel.getDriverList.observe(this, Observer {
            if (!it.error) {
                val deviceIdArr = ArrayList<String>()
                for (i in 0 until it.data.size) {
                    if (it.data[i].device_token != null) {
                        Log.e("CHECK_NULL", "device - ${it.data[i].device_token}")
                        deviceIdArr.add(it.data[i].device_token)
                    }
                }
                App.sendNotification(this, deviceIdArr, App.getUserID(this))
            }
        })
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}