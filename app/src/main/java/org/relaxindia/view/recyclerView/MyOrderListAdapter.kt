package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_my_order_list.view.*

import org.relaxindia.R
import org.relaxindia.view.activity.BookingInfoActivity


class MyOrderListAdapter(context: Context) : RecyclerView.Adapter<MyOrderListAdapter.ViewHolder>() {
    val context = context

    private var booking: ArrayList<String> = ArrayList()

    fun updateData(book: List<String>) {
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
        fun bind(booking: String) {

            view.view_booking_details.setOnClickListener {
                val intent = Intent(view.context, BookingInfoActivity::class.java)
                view.context.startActivity(intent)
            }

        }
    }
}
