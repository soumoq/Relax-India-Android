package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_opsonal_service.view.*

import org.relaxindia.R
import org.relaxindia.model.getService.ServiceData
import org.relaxindia.util.App
import org.relaxindia.util.loadImage
import org.relaxindia.view.activity.BookNowActivity


class OptionalServiceAdapter(context: Context) :
    RecyclerView.Adapter<OptionalServiceAdapter.ViewHolder>() {
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
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_opsonal_service, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(service[position], position, context)
    }

    override fun getItemCount() = service.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(service: ServiceData, position: Int, context: Context) {
            view.op_service_image.loadImage(service.image)
            view.op_service_name.text = service.name
            view.op_service_price.text = "${App.rs}${service.price.toString()}"

            if (service.select){
                view.recycler_op_service_layout.setBackgroundColor(Color.parseColor("#EAECEE"))
            }else{
                view.recycler_op_service_layout.setBackgroundColor(Color.parseColor("#ffffff"))
            }

            view.recycler_op_service_layout.setOnClickListener {
                if (service.select) {
                    service.select = false
                    view.recycler_op_service_layout.setBackgroundColor(Color.parseColor("#ffffff"))
                    (view.context as BookNowActivity).updatePrice(false,service.id)
                } else {
                    service.select = true
                    view.recycler_op_service_layout.setBackgroundColor(Color.parseColor("#EAECEE"))
                    (view.context as BookNowActivity).updatePrice(true,service.id)

                }
            }

        }
    }
}
