package org.relaxindia.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_transactions.*
import org.relaxindia.R
import org.relaxindia.model.TransactionList
import org.relaxindia.service.VollyApi
import org.relaxindia.util.toast

class TransactionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        wallet_back.setOnClickListener {
            onBackPressed()
        }

        VollyApi.getTransaction(this)

    }

    fun getTransactionList(transactionList: ArrayList<TransactionList>) {
        toast(transactionList.size.toString())
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