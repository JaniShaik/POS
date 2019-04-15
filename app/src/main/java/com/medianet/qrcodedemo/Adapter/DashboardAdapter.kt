package com.medianet.qrcodedemo.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.medianet.qrcodedemo.R

class DashboardAdapter(private val context: Context, private val imgList: ArrayList<Int>, private val nameList: ArrayList<String>,
                       private val userClickCallbacks: UserClickCallbacks, private val type: Int) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    interface UserClickCallbacks {
        fun onUserClick(position: Int)
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.grid_single, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var textGrid: TextView? = null
        private var imgGrid: ImageView? = null

        init {
            itemView.setOnClickListener(this)
            textGrid = itemView.findViewById<TextView>(R.id.textGrid)
            imgGrid = itemView.findViewById<ImageView>(R.id.imgGrid)
        }

        fun bindItem(position: Int) {
            textGrid?.text = nameList[position]
            imgGrid?.setImageResource(imgList[position])
        }

        // Item Click Listener
        override fun onClick(position: View?) {
            userClickCallbacks.onUserClick(adapterPosition)
        }
    }

}