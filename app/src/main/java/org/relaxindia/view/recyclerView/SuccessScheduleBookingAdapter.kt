package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.success_schedule_booking_list.view.*

import org.relaxindia.R
import org.relaxindia.SuccessScheduleReq


class SuccessScheduleBookingAdapter(context: Context) :
    RecyclerView.Adapter<SuccessScheduleBookingAdapter.ViewHolder>() {
    val context = context

    private var booking: ArrayList<SuccessScheduleReq> = ArrayList()

    fun updateData(book: List<SuccessScheduleReq>) {
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
        LayoutInflater.from(parent.context).inflate(R.layout.success_schedule_booking_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(booking.get(position), context)
    }

    override fun getItemCount() = booking.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(booking: SuccessScheduleReq, context: Context) {

            view.success_schedule_source_loc.text = booking.from_location
            view.success_schedule_des_loc.text = booking.to_location
            view.success_schedule_booking_status.text = "Status : ${booking.status}"
            view.success_schedule_booking_comment.text = "User comment : ${booking.user_comment}"
            view.success_schedule_booking_date_time.text = "Date Time : ${booking.schedule_date_time}"
            view.success_schedule_booking_base_pay.text = booking.total_amount.toString()
            view.success_schedule_booking_total_amt.text = booking.booking_amount.toString()
            view.success_schedule_booking_driver_pay.text =
                "${booking.total_amount - booking.booking_amount}"


        }
    }
}
