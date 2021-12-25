package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.schedule_booking_list.view.*

import org.relaxindia.R
import org.relaxindia.model.ScheduleReq
import org.relaxindia.view.activity.StartScheduleBookingActivity


class ScheduleBookingAdapter(context: Context) :
    RecyclerView.Adapter<ScheduleBookingAdapter.ViewHolder>() {
    val context = context

    private var booking: ArrayList<ScheduleReq> = ArrayList()

    fun updateData(book: List<ScheduleReq>) {
        this.booking.clear()
        this.booking.addAll(book)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.schedule_booking_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(booking.get(position), context)
    }

    override fun getItemCount() = booking.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(booking: ScheduleReq, context: Context) {

            view.schedule_source_loc.text = booking.from_location
            view.schedule_des_loc.text = booking.to_location
            view.schedule_booking_status.text = "Status : ${booking.status}"
            view.schedule_booking_comment.text = "User comment : ${booking.user_comment}"
            view.schedule_booking_date_time.text = "Date Time : ${booking.schedule_date_time}"
            view.schedule_booking_base_pay.text = booking.total_amount.toString()
            view.schedule_booking_total_amt.text = booking.booking_amount.toString()
            view.schedule_booking_driver_pay.text =
                "${booking.total_amount - booking.booking_amount}"

            if (booking.status == "Awaiting") {
                view.payment_layout.visibility = View.GONE
                view.pay_now_button.visibility = View.GONE

            } else if (booking.status == "Pay") {
                view.payment_layout.visibility = View.VISIBLE
                view.pay_now_button.visibility = View.VISIBLE
            } else {
                view.payment_layout.visibility = View.VISIBLE
                view.pay_now_button.visibility = View.GONE
            }

            view.pay_now_button.setOnClickListener {
                (context as StartScheduleBookingActivity).startPayment(
                    booking.booking_amount,
                    booking.booking_id.toString()
                )
            }
        }
    }
}
