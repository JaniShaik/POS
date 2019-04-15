package com.medianet.qrcodedemo.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.medianet.qrcodedemo.Adapter.DetailsAdapter
import com.medianet.qrcodedemo.Constants
import com.medianet.qrcodedemo.R
import com.medianet.qrcodedemo.Retrofit.DataClass.ProductDetails
import kotlinx.android.synthetic.main.activity_details.*
import java.io.Serializable

class DetailsActivity : AppCompatActivity(), DetailsAdapter.OnQuantityChange {
    // Context
    val mContext: Context = this
    // Bill Details
    var mBillDetails = ArrayList<ProductDetails>()
    // RecyclerView
    private var detailsAdapter: DetailsAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    // Total
    var totalPrice = 0.00
    var quantity = 0
    var vat = 0.18
    // OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        // Data
        val data = intent.extras
        if (data != null) {
            mBillDetails = data.getSerializable("bill_details") as ArrayList<ProductDetails>
        }
        // RecyclerView
        detailsAdapter = DetailsAdapter(mContext, mBillDetails, this)
        layoutManager = LinearLayoutManager(mContext)
        productRecyclerView.layoutManager = layoutManager
        productRecyclerView.adapter = detailsAdapter
        detailsAdapter!!.notifyDataSetChanged()
        //
        displayPriceDetails(1)
        /*// Price Quantity
        val qtyItem = "Price (" + Math.abs(quantity) + "item)"
        priceItemTV.text = qtyItem
        priceItemTotalTV.text = Constants.getCurrencyFormat(totalBill(mBillDetails, 1).toString())
        // Tax
        val tax = totalPrice * vat
        taxTV.text = Constants.getCurrencyFormat(tax.toString())
        // Total Price
        val totalPrice = totalBill(mBillDetails, 2).plus(tax)
        billTV.text = Constants.getCurrencyFormat(totalPrice.toString())*/
        //
        btn_add_bill.setOnClickListener {
            navigateToScanIntent()
        }
        //
        btn_new_bill.setOnClickListener {
            mBillDetails.clear()
            //
            productRecyclerView.visibility = View.GONE
            //
            displayPriceDetails(1)
            // Total Price
            /*billTV.text = Constants.getCurrencyFormat(totalBill(mBillDetails, 1).toString())
            val qty_iItem = "Price (" + Math.abs(quantity) + "item)"
            priceItemTV.text = qty_iItem
            priceItemTotalTV.text = Constants.getCurrencyFormat(totalBill(mBillDetails, 1).toString())
            detailsAdapter!!.notifyDataSetChanged()*/
        }
        // Bill Details
        btn_confirm_bill.setOnClickListener {
            if (mBillDetails.size > 0) {
                val intent = Intent(mContext, BillDetailsActivity::class.java)
                intent.putExtra("bill_details", mBillDetails as Serializable)
                startActivity(intent)
            } else {
                Toast.makeText(mContext, "No products scanned...!", Toast.LENGTH_SHORT).show()
            }

        }
        // Nav bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Bill Details"
    }

    private fun displayPriceDetails(type: Int) {
        val total = totalBill(mBillDetails, type)
        // Price Quantity
        val qtyItem = "Price (" + Math.abs(quantity) + " item)"
        priceItemTV.text = qtyItem
        priceItemTotalTV.text = Constants.getCurrencyFormat(totalBill(mBillDetails, 1).toString())
        // Tax
        val tax = totalPrice * vat
        taxTV.text = Constants.getCurrencyFormat(tax.toString())
        // Total Price
        val totalPrice = total.plus(tax)
        billTV.text = Constants.getCurrencyFormat(totalPrice.toString())
    }

    // Increase Quantity
    override fun increaseQuantity(productDetails: ProductDetails, position: Int) {
        val product_details = mBillDetails[position]
        // Quantity
        var qty = product_details.qty
        qty += 1
        productDetails.qty = qty
        // Total Price
        val total = product_details.totalPrice.plus(product_details.price.toDouble())
        productDetails.totalPrice = total
        // Updating
        mBillDetails[position] = product_details
        detailsAdapter!!.notifyDataSetChanged()
        // Details
        displayPriceDetails(1)
    }

    // Decrease Quantity
    override fun decreaseQuantity(productDetails: ProductDetails, position: Int) {
        val product_details = mBillDetails[position]
        // Quantity
        var qty = product_details.qty
        if (qty > 1) {
            qty -= 1
            productDetails.qty = qty
            // Total Price
            val total = product_details.totalPrice.minus(product_details.price.toDouble())
            productDetails.totalPrice = total
            // Updating
            mBillDetails[position] = product_details
        } else {
            mBillDetails.removeAt(position)
        }
        detailsAdapter!!.notifyDataSetChanged()
        //
        displayPriceDetails(2)
        /*// Price Quantity
        val qtyItem = "Price (" + Math.abs(quantity) + " item)"
        priceItemTV.text = qtyItem
        priceItemTotalTV.text = Constants.getCurrencyFormat(totalBill(mBillDetails, 1).toString())
        // Tax
        val tax = totalPrice * vat
        taxTV.text = Constants.getCurrencyFormat(tax.toString())
        // Total Price
        val totalPrice = totalBill(mBillDetails, 2).plus(tax)
        billTV.text = Constants.getCurrencyFormat(totalPrice.toString())*/
    }

    // Total Price
    private fun totalBill(productList: ArrayList<ProductDetails>, type: Int): Double {
        totalPrice = 0.00
        quantity = 0
        if (type == 1) {
            for (details in productList) {
                totalPrice += details.totalPrice
                quantity += details.qty
            }
        } else if (type == 2) {
            for (details in productList) {
                totalPrice -= details.totalPrice
                quantity -= details.qty
            }
        }
        return Math.abs(totalPrice)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                navigateToScanIntent()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        navigateToScanIntent()
        finish()
    }

    private fun navigateToScanIntent() {
        val intent = Intent(mContext, ScanActivity::class.java)
        intent.putExtra("bill_details", mBillDetails as Serializable)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
