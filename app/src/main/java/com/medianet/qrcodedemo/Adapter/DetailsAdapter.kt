package com.medianet.qrcodedemo.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.medianet.qrcodedemo.Constants
import com.medianet.qrcodedemo.R
import com.medianet.qrcodedemo.Retrofit.DataClass.ProductDetails

class DetailsAdapter(private val mContext: Context, private var productList: ArrayList<ProductDetails>, private var onClick: OnQuantityChange)
    : RecyclerView.Adapter<DetailsAdapter.ViewHolder>() {

    interface OnQuantityChange {
        fun increaseQuantity(productDetails: ProductDetails, position: Int)
        fun decreaseQuantity(productDetails: ProductDetails, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(productList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var productNameTV: TextView = itemView.findViewById(R.id.productNameTV)
        var qtyTV: TextView = itemView.findViewById(R.id.qtyTV)
        var addIMG: ImageView = itemView.findViewById(R.id.addIMG)
        var deleteIMG: ImageView = itemView.findViewById(R.id.deleteIMG)
        var productCodeTV: TextView = itemView.findViewById(R.id.productCodeTV)
        var productPriceTV: TextView = itemView.findViewById(R.id.productPriceTV)

        init {
            addIMG.setOnClickListener(this)
            deleteIMG.setOnClickListener(this)
        }

        fun bindItem(productDetails: ProductDetails) {
            val sNo = adapterPosition + 1
            val name = sNo.toString() + ". " + productDetails.name
            productNameTV.text = name
            qtyTV.text = productDetails.qty.toString()
            productCodeTV.text = productDetails.code
            productPriceTV.text = Constants.getCurrencyFormat(productDetails.totalPrice.toString())
        }

        override fun onClick(v: View?) {
            val productDetails = productList[adapterPosition]
            when (v?.id) {
                R.id.addIMG -> {
                    onClick.increaseQuantity(productDetails, adapterPosition)
                }
                R.id.deleteIMG -> {
                    onClick.decreaseQuantity(productDetails, adapterPosition)
                }
            }
        }

    }
}