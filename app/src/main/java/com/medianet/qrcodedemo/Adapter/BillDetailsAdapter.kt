package com.medianet.qrcodedemo.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.medianet.qrcodedemo.Constants
import com.medianet.qrcodedemo.R
import com.medianet.qrcodedemo.Retrofit.DataClass.ProductDetails

class BillDetailsAdapter(private val mContext: Context, private var productList: ArrayList<ProductDetails>)
    : RecyclerView.Adapter<BillDetailsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(productList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.bill_product_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productNameTV: TextView = itemView.findViewById(R.id.productNameTV)
        var qtyTV: TextView = itemView.findViewById(R.id.qtyTV)
        var productPriceTV: TextView = itemView.findViewById(R.id.productPriceTV)

        fun bindItem(productDetails: ProductDetails) {
            val sNo = adapterPosition + 1
            val name = sNo.toString() + ". " + productDetails.name
            val quantity = "Qty: " + productDetails.qty.toString()
            productNameTV.text = name
            qtyTV.text = quantity
            productPriceTV.text = Constants.getCurrencyFormat(productDetails.totalPrice.toString())
        }
    }
}