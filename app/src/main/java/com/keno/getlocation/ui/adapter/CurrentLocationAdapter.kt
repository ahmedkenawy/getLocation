package com.keno.getlocation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.keno.getlocation.R
import com.keno.getlocation.model.CurrentLocation

class CurrentLocationAdapter(var locations: ArrayList<CurrentLocation>) :
    RecyclerView.Adapter<CurrentLocationAdapter.CurrentLocationViewHolder>() {

    fun setData(newLocations: ArrayList<CurrentLocation>) {
        locations = newLocations
        notifyDataSetChanged()
    }


    class CurrentLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(location: CurrentLocation) {
            itemView.findViewById<TextView>(R.id.lat).text = location.lat
            itemView.findViewById<TextView>(R.id.lon).text = location.lng
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentLocationViewHolder {
        return CurrentLocationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        )
    }

    override fun getItemCount(): Int = locations.size

    override fun onBindViewHolder(holder: CurrentLocationViewHolder, position: Int) {
        holder.bind(locations[position])
    }
}