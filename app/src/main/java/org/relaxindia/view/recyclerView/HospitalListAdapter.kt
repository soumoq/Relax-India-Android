package org.relaxindia.view.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_hospital_list.view.*
import kotlinx.android.synthetic.main.recycler_opsonal_service.view.*
import kotlinx.android.synthetic.main.recycler_opsonal_service.view.op_service_image

import org.relaxindia.R
import org.relaxindia.model.HospitalList
import org.relaxindia.util.App
import org.relaxindia.util.loadImage
import org.relaxindia.view.activity.BookNowActivity


class HospitalListAdapter(context: Context) :
    RecyclerView.Adapter<HospitalListAdapter.ViewHolder>() {
    val context = context

    private var service: ArrayList<HospitalList> = ArrayList()

    fun updateData(services: List<HospitalList>) {
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
            .inflate(R.layout.recycler_hospital_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(service[position], position, context)
    }

    override fun getItemCount() = service.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(hospital: HospitalList, position: Int, context: Context) {
            view.hospital_text.text = hospital.name


        }
    }
}
