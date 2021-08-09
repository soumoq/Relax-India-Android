package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_service.view.*

import org.relaxindia.R
import org.relaxindia.model.getService.ServiceData
import org.relaxindia.util.toast
import org.relaxindia.view.activity.HomeActivity


class ServiceAdapter(context: Context) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {
    val context = context

    private var service: ArrayList<ServiceData> = ArrayList()

    fun updateData(services: List<ServiceData>) {
        this.service.clear()
        this.service.addAll(services)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_service, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(service[position], position,context)
    }

    override fun getItemCount() = service.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(service: ServiceData, position: Int, context: Context) {

            view.service_name.text = service.name
            if (service.select) {
                view.service_liner_layout.setBackgroundColor(Color.parseColor("#00b0ec"))
                view.service_name.setTextColor(Color.parseColor("#ffffff"))
                view.service_time.setTextColor(Color.parseColor("#ffffff"))
            } else {
                view.service_liner_layout.setBackgroundColor(Color.parseColor("#EAECEE"))
                view.service_name.setTextColor(Color.parseColor("#1f2a43"))
                view.service_time.setTextColor(Color.parseColor("#1f2a43"))
            }


            view.recycler_library_service_layout.setOnClickListener {
                (context as HomeActivity).changeBackGround(position)

            }

        }
    }
}
