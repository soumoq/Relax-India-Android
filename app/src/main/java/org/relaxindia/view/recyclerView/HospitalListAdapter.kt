package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_hospital_list.view.*

import org.relaxindia.R
import org.relaxindia.model.HospitalList
import org.relaxindia.view.activity.HomeActivity


class HospitalListAdapter(context: Context) :
    RecyclerView.Adapter<HospitalListAdapter.ViewHolder>() {
    val context = context

    private var service: ArrayList<HospitalList> = ArrayList()

    fun updateData(services: List<HospitalList>) {
        this.service.clear()
        this.service.addAll(services)
        notifyDataSetChanged()
    }

    fun updateClick(pos: Int) {
        for (i in 0 until service.size) {
            if (i == pos) {
                service[i].click = true
            } else if (i != pos) {
                service[i].click = false
            }
        }
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
            .inflate(R.layout.recycler_hospital_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(service[position], position, context)
    }

    override fun getItemCount() = service.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(
            hospital: HospitalList,
            position: Int,
            context: Context
        ) {
            if (hospital.click) {
                view.hospital_text.setTextColor(ContextCompat.getColor(context, R.color.app_color))
                view.hospital_list_layout.background =
                    ContextCompat.getDrawable(context, R.drawable.hos_box)
            } else {
                view.hospital_text.setTextColor(ContextCompat.getColor(context, R.color.black))
                view.hospital_list_layout.background =
                    ContextCompat.getDrawable(context, R.drawable.hos_box_f)

            }
            view.hospital_text.text = hospital.name
            view.hospital_list_layout.setOnClickListener {
                (context as HomeActivity).getClickHospital(position)
                (view.context as HomeActivity).getSearch(hospital.lat, hospital.lon, hospital.name)
            }

        }
    }
}
