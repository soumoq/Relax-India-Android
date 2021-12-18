package org.relaxindia.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_support.*
import org.relaxindia.R
import org.relaxindia.service.VollyApi
import org.relaxindia.util.toast

class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        support_back.setOnClickListener {
            onBackPressed()
        }

        support_submit.setOnClickListener {
            if (support_topic.text?.isNotEmpty()!! && support_description.text?.isNotEmpty()!!) {
                VollyApi.raiseToken(
                    this,
                    support_topic.text.toString(),
                    support_description.text.toString()
                )
            } else {
                toast("Please enter valid input!!!")
            }
        }


    }

    fun reloadActivity() {
        finish();
        startActivity(intent);
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