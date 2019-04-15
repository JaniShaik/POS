package com.medianet.qrcodedemo.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.barcode.Barcode
import com.medianet.qrcodedemo.Constants
import com.medianet.qrcodedemo.NetworkInterceptor.NoConnectivityException
import com.medianet.qrcodedemo.R
import com.medianet.qrcodedemo.Retrofit.ApiService
import com.medianet.qrcodedemo.Retrofit.DataClass.Product
import com.medianet.qrcodedemo.Retrofit.DataClass.ProductDetails
import info.androidhive.barcode.BarcodeReader
import kotlinx.android.synthetic.main.activity_scan.*
import retrofit2.Call
import retrofit2.Callback
import java.io.Serializable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

class ScanActivity : AppCompatActivity(), BarcodeReader.BarcodeReaderListener {
    // Context
    val mContext: Context = this
    // Barcode Scanner
    var mBarcodeReader: BarcodeReader? = null
    // ArrayList
    var mScanDetails = ArrayList<String?>()
    var mBillDetails = ArrayList<ProductDetails>()
    // API URL
    val API_URL = "http://202.53.92.122/jani/"
    // Product Price
    var productPrice = 0.00
    // Quantity
    var quantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        // get barcode reader instance
        mBarcodeReader = supportFragmentManager.findFragmentById(R.id.barcode_scanner) as BarcodeReader?
        // Initial Price
        amountTV.text = Constants.getCurrencyFormat(productPrice.toString())
        qtyTV.text = quantity.toString()
        // Bill Details
        btn_view_bill.setOnClickListener {
            /*val intent = Intent(mContext, DetailsActivity::class.java)
            intent.putExtra("bill_details", mBillDetails as Serializable)
            startActivityForResult(intent, 1)*/
            if(mScanDetails.size > 0) {
                val intent = Intent(mContext, DetailsActivity::class.java)
                intent.putExtra("bill_details", mBillDetails as Serializable)
                startActivityForResult(intent, 1)
            } else {
                Toast.makeText(mContext, "No products scanned...!", Toast.LENGTH_SHORT).show()
            }
        }
        // Nav bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Scan Product"
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onScannedMultiple(barcodes: MutableList<Barcode>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onScanned(barcode: Barcode?) {
        // playing barcode reader beep sound
        mBarcodeReader?.playBeep()
        barcode?.displayValue.let {
            mScanDetails.add(it)
            getProductDetails()
        }
        //Log.e("Scanned Msg", barcode?.displayValue)
        //Toast.makeText(mContext, barcode?.displayValue, Toast.LENGTH_LONG).show()
    }

    override fun onScanError(errorMessage: String?) {
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + errorMessage, Toast.LENGTH_SHORT).show();
    }


    /**
     * Product Details
     */
    private fun getProductDetails() {
        /* val mProgressDialog = ProgressDialog(mContext)
         mProgressDialog.isIndeterminate = true
         mProgressDialog.setMessage("fetching product details...")
         mProgressDialog.show()*/
        val requestInterface = ApiService.create(API_URL, mContext)
        val productCall = requestInterface.getProductDetails()

        productCall.enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: retrofit2.Response<Product>) {
                //mProgressDialog.dismiss()
                if (response.isSuccessful()) {
                    val productResponse = response.body()
                    if (productResponse!!.status) {
                        mBillDetails.add(productResponse.response)
                        productPrice += productResponse.response.price.toDouble()
                        quantity += 1
                        //val proPrice = String.format("%.2f", productPrice).toDouble()
                        val proPrice = Math.round(productPrice * 100.0) / 100.0
                        amountTV.text = Constants.getCurrencyFormat(proPrice.toString())
                        qtyTV.text = quantity.toString()
                    } else {
                        Toast.makeText(mContext, "Product details not available...!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Error Case

                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Log.e("TAG", "onFailure: " + t.toString())
                //mProgressDialog.dismiss()
                if (t is NoConnectivityException) {
                    Toast.makeText(mContext, "No Internet Connection..!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    productPrice = 0.00
                    quantity = 0
                    mBillDetails.clear()
                    mBillDetails.addAll(data?.getSerializableExtra("bill_details") as ArrayList<ProductDetails>)
                    //
                    amountTV.text = Constants.getCurrencyFormat(totalBill(mBillDetails).toString())
                    qtyTV.text = quantity.toString()
                }
            }
        }
    }

    // Total Price
    private fun totalBill(productList: ArrayList<ProductDetails>): Double {
        for (details in productList) {
            productPrice += details.totalPrice
            quantity += details.qty
        }
        return productPrice
    }

}
