package org.relaxindia.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_my_booking.*
import org.relaxindia.R
import org.relaxindia.view.recyclerView.MyOrderListAdapter

class MyBookingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_booking)

        my_booking_back.setOnClickListener {
            onBackPressed()
        }

        val myOrderListAdapter = MyOrderListAdapter(this)
        my_booking_list.adapter = myOrderListAdapter

        val arrayList = ArrayList<String>()
        arrayList.add("")
        arrayList.add("")
        arrayList.add("")
        arrayList.add("")
        myOrderListAdapter.updateData(arrayList)

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