package org.relaxindia.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_my_booking.*
import org.relaxindia.R
import org.relaxindia.util.toast
import org.relaxindia.view.recyclerView.MyOrderListAdapter
import org.relaxindia.viewModel.ApiCallViewModel

class MyBookingActivity : AppCompatActivity() {

    lateinit var apiCallViewModel: ApiCallViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_booking)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()
        apiCallViewModel.getBookingInfo(this)


        my_booking_back.setOnClickListener {
            onBackPressed()
        }


    }

    private fun observeViewModel() {
        apiCallViewModel.getBookingHistory.observe(this, Observer {
            if (!it.error) {
                val myOrderListAdapter = MyOrderListAdapter(this)
                my_booking_list.adapter = myOrderListAdapter
                myOrderListAdapter.updateData(it.data)
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
    }

}