package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_my_order_list.view.*

import org.relaxindia.R
import org.relaxindia.model.bookingHistory.BookingListData
import org.relaxindia.util.loadImage
import org.relaxindia.view.activity.BookingInfoActivity
import org.relaxindia.view.activity.DriverFeedbackActivity


class MyOrderListAdapter(context: Context) : RecyclerView.Adapter<MyOrderListAdapter.ViewHolder>() {
    val context = context

    private var booking: ArrayList<BookingListData> = ArrayList()

    fun updateData(book: List<BookingListData>) {
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
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_my_order_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(booking.get(position))
    }

    override fun getItemCount() = booking.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(booking: BookingListData) {

            var styledText = ""
            when {
                booking.status.equals("Pending") -> styledText =
                    "Status: <font color='#F7DC6F'>${booking.status}</font>"
                booking.status.equals("Booked") -> styledText =
                    "Status: <font color='#1ABC9C'>${booking.status}</font>"
                booking.status.equals("Cancelled") -> styledText =
                    "Status: <font color='red'>${booking.status}</font>"
                booking.status.equals("Refund") -> styledText =
                    "Status: <font color='#2980B9'>${booking.status}</font>"
            }

            if (booking.status.equals("Booked")) {
                view.my_order_details_layout.visibility = View.VISIBLE
                view.recycler_library_item_layout.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        view.context,
                        R.color.white
                    )
                )
            } else {
                view.my_order_details_layout.visibility = View.GONE
                view.recycler_library_item_layout.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        view.context,
                        R.color.light_light_gray
                    )
                )
            }


            view.booked_status.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE)
            view.order_list_des_loc.text = booking.to_location
            view.order_list_source_loc.text = booking.from_location
            view.order_list_driver_name.text = booking.driver_name
            view.order_list_date.text = booking.date
            view.order_list_driver_image.loadImage(booking.driver_image)

            view.view_booking_details.setOnClickListener {
                val intent = Intent(view.context, BookingInfoActivity::class.java)
                intent.putExtra("service_details", booking.service_deatils)
                intent.putExtra("driver_name", booking.driver_name)
                intent.putExtra("from_location", booking.from_location)
                intent.putExtra("to_location", booking.to_location)
                intent.putExtra("driver_image", booking.driver_image)
                intent.putExtra("date", booking.date)
                intent.putExtra("booking_amount", booking.booking_amount)
                intent.putExtra("total_amount", booking.total_amount.toString())
                intent.putExtra("from_latitude", booking.from_latitude.toString())
                intent.putExtra("from_longitude", booking.from_longitude.toString())
                intent.putExtra("to_latitude", booking.to_latitude.toString())
                intent.putExtra("to_longitude", booking.to_longitude.toString())
                intent.putExtra("driver_id", booking.driver_id.toString())
                intent.putExtra("driver_phone", booking.driver_phone.toString())


                view.context.startActivity(intent)
            }

            view.driver_info.setOnClickListener {
                val intent = Intent(view.context, DriverFeedbackActivity::class.java)
                intent.putExtra("booking_id", booking.booking_id.toString())
                intent.putExtra("driver_name", booking.driver_name)
                intent.putExtra("driver_id", booking.driver_id.toString())
                intent.putExtra("from_location", booking.from_location)
                intent.putExtra("to_location", booking.to_location)
                intent.putExtra("driver_image", booking.driver_image)
                view.context.startActivity(intent)
            }

        }
    }
}
