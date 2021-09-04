package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_other_service.view.*
import org.json.JSONArray

import org.relaxindia.R


class OtherServiceAdapter(context: Context) :
    RecyclerView.Adapter<OtherServiceAdapter.ViewHolder>() {
    val context = context

    private var service = JSONArray()

    fun updateData(services: JSONArray) {
        this.service = services
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
            .inflate(R.layout.recycler_other_service, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(service, position)
    }

    override fun getItemCount() = service.length()

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(service: JSONArray, position: Int) {
            view.other_service_name.text = service.getJSONObject(position).getString("name")
            view.other_service_price.text = service.getJSONObject(position).getString("price")

        }
    }
}
