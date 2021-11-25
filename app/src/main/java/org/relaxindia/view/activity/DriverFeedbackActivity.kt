package org.relaxindia.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_driver_feedback.*
import org.relaxindia.R
import org.relaxindia.service.VollyApi
import org.relaxindia.util.loadImage
import org.relaxindia.util.toast

class DriverFeedbackActivity : AppCompatActivity() {

    private var bookingId = ""
    private var driverName = ""
    private var driverId = ""
    private var fromLocation = ""
    private var toLocation = ""
    private var driverImage = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_feedback)

        bookingId = intent.getStringExtra("booking_id").toString()
        driverName = intent.getStringExtra("driver_name").toString()
        driverId = intent.getStringExtra("driver_id").toString()
        fromLocation = intent.getStringExtra("from_location").toString()
        toLocation = intent.getStringExtra("to_location").toString()
        driverImage = intent.getStringExtra("driver_image").toString()

        driver_name_feedback.text = driverName
        from_location_feedback.text = fromLocation
        to_location_feedback.text = toLocation
        profile_image_feedback.loadImage(driverImage)



        feedback_submit.button.setOnClickListener {
            //toast("${feedback_rating.rating.toInt()}" + " " + bookingId)
            if (feedback_rating.rating.toInt() != 0) {
                VollyApi.giveReating(
                    this,
                    bookingId,
                    feedback_rating.rating.toString(),
                    feedback_review.text.toString()
                )
            } else {
                toast("Invalid input!!!")
            }

        }

    }

    fun sendThanksActivity() {
        val intent = Intent(this, ThankYouActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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