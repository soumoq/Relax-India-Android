package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_support_list.view.*

import org.relaxindia.R
import org.relaxindia.model.SupportList


class SupportListAdapter(context: Context) :
    RecyclerView.Adapter<SupportListAdapter.ViewHolder>() {
    val context = context

    private var support: ArrayList<SupportList> = ArrayList()

    fun updateData(supportList: List<SupportList>) {
        this.support.clear()
        this.support.addAll(supportList)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_support_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(support.get(position), context)
    }

    override fun getItemCount() = support.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(support: SupportList, context: Context) {
            /*
            String styledText = "This is <font color='red'>simple</font>.";
            textView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            */

            view.support_list_status.text = "Status: ${support.status}"
            view.support_list_topic.text = "Topic: ${support.topic}"
            view.support_list_dec.text = "Description: ${support.description}"

        }
    }
}
