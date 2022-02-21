package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_transaction_list.view.*

import org.relaxindia.R
import org.relaxindia.model.TransactionList


class TransactionListAdapter(context: Context) :
    RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {
    val context = context

    private var transaction: ArrayList<TransactionList> = ArrayList()

    fun updateData(transactionList: List<TransactionList>) {
        this.transaction.clear()
        this.transaction.addAll(transactionList)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_transaction_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transaction.get(position), context)
    }

    override fun getItemCount() = transaction.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(transaction: TransactionList, context: Context) {
            /*
            String styledText = "This is <font color='red'>simple</font>.";
            textView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            */

            view.transaction_status.text = "Status : ${transaction.status}"
            view.transaction_amount_paid.text =
                "Amount paid : ${transaction.amount_paid}"
            view.transaction_journey_total_amount.text =
                "Journey total amount : ${transaction.journey_total_amount}"
            view.transaction_booking_type.text = "Booking type : ${transaction.booking_type}"
            view.transaction_date.text = "Date : ${transaction.date}"

        }
    }
}
