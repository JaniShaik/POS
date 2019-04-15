package com.medianet.qrcodedemo.Activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.medianet.qrcodedemo.Adapter.BillDetailsAdapter
import com.medianet.qrcodedemo.AidlUtil
import com.medianet.qrcodedemo.BaseApp
import com.medianet.qrcodedemo.Constants
import com.medianet.qrcodedemo.R
import com.medianet.qrcodedemo.Retrofit.DataClass.ProductDetails
import kotlinx.android.synthetic.main.activity_bill_details.*
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class BillDetailsActivity : AppCompatActivity() {
    // Context
    val mContext: Context = this
    // Bill Details
    var mBillDetails = ArrayList<ProductDetails>()
    // RecyclerView
    private var detailsAdapter: BillDetailsAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    // Total
    var totalPrice = 0.00
    var quantity = 0
    var vat = 0.18
    //
    private var billString = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_details)
        //
        val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: NestedScrollView = inflater.inflate(R.layout.activity_bill_details, null) as NestedScrollView
        view.setDrawingCacheEnabled(true)
        //val screen: Bitmap = getBitmapFromView(this.getWindow().findViewById(R.id.scrollView))
        // Data
        val data = intent.extras
        if (data != null) {
            mBillDetails = data.getSerializable("bill_details") as ArrayList<ProductDetails>
        }
        // RecyclerView
        detailsAdapter = BillDetailsAdapter(mContext, mBillDetails)
        layoutManager = LinearLayoutManager(mContext)
        detailsRecyclerView.layoutManager = layoutManager
        detailsRecyclerView.adapter = detailsAdapter
        detailsAdapter!!.notifyDataSetChanged()
        //
        displayPriceDetails(1)
        //
        btn_print_bill.setOnClickListener {
            prepareString()
            //val stringData = billString.toString().toByteArray()
            //AidlUtil.getInstance().sendRawData(stringData)
            //takeScreenshot()
            //AidlUtil.getInstance().printText("Test printing", 24.0f, false, false)
            //if (baseApp.isAidl()) run { AidlUtil.getInstance().printText("Test printing", 14.0f, false, false) }
        }
        // Nav bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Bill Details"
    }

    // Date
    private fun dateFormat(): String {
        val cal = Calendar.getInstance()
        val date = cal.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss ")
        val formattedDate = dateFormat.format(date)
        return formattedDate
    }

    //
    private fun prepareString() {
        // Heading
        val heading = StringBuilder()
        heading.append("MEDIANET QRCODE MANUFACTURES\n")
        heading.append("GERMANY, EUROPE\n")
        // Content
        billString.append("*******************************\n")
        billString.append("Bill No: 10010 \n")
        billString.append("Bill Date: " + dateFormat() + "\n")
        billString.append("-------------------------------\n")
        // Item - 13, Qty - 4, Amount - 13
        billString.append("Item         " + "Qty " + "       Amount" + "\n")
        billString.append("-------------------------------\n")
        for (product in mBillDetails) {
            var name = product.name
            var qty = product.qty.toString()
            var price = Constants.twoDecimals(product.totalPrice.toString())
            // Name
            if (name.length > 12) {
                name = name.substring(0, 12)
            } else if (name.length < 12) {
                for (i in name.length..12) {
                    name += " "
                }
            }
            // Quantity
            if (qty.length > 3) {
                qty = qty.substring(0, 3)
            } else if (qty.length < 3) {
                for (i in qty.length..3) {
                    qty = " " + qty
                }
            } else {
                qty = product.qty.toString()
            }
            // Price
            if (price.length > 12) {
                price = price.substring(0, 12)
            } else if (price.length < 12) {
                for (i in price.length..12) {
                    price = " " + price
                }
            }
            billString.append(name + qty + price + "\n")
        }
        billString.append("-------------------------------\n")
        // Sub Total
        var subTotal = "Sub Total: " + Constants.twoDecimals(totalBill(mBillDetails, 1).toString())
        if (subTotal.length < 30) {
            for (i in subTotal.length..30) {
                subTotal = " " + subTotal
            }
        }
        billString.append(subTotal + "\n")
        // Tax
        val taxCal = totalPrice * vat
        var tax = "Tax(18%): " + Constants.twoDecimals(taxCal.toString())
        if (tax.length < 30) {
            for (i in tax.length..30) {
                tax = " " + tax
            }
        }
        billString.append(tax + "\n")
        billString.append("-------------------------------\n")
        // Total
        val totalBuilder = StringBuilder()
        val totalP = totalBill(mBillDetails, 1)
        val totalPrice = totalP.plus(taxCal)
        val total = "Total: " + Constants.twoDecimals(totalPrice.toString())
        /*if (total.length < 30) {
            for (i in total.length..30) {
                total = " " + total
            }
        }*/
        totalBuilder.append(total + "\n")
        // Footer
        val footerBuilder = StringBuilder()
        footerBuilder.append("-------------------------------\n")
        footerBuilder.append("#EMP10039\n")
        footerBuilder.append("Thank You. Visit Again\n\n\n\n")
        //
        val contentData = billString.toString().toByteArray()
        val footerData = footerBuilder.toString().toByteArray()
        //AidlUtil.getInstance().sendRawData(stringData)
        AidlUtil.getInstance().prepareDataToPrint(heading.toString(), contentData, totalBuilder.toString(), footerData)
    }

    // To take screenshot
    private fun takeScreenshot() {
        val screenView: View = findViewById(R.id.scrollView)
        val nestedScrollView: NestedScrollView = findViewById<NestedScrollView>(R.id.scrollView) as NestedScrollView
        val totalHeight: Int = nestedScrollView.getChildAt(0).height
        val totalWidth: Int = nestedScrollView.getChildAt(0).width
        val bitmap: Bitmap = getBitmapFromView(screenView, totalHeight, totalWidth)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        // Change bitmap image size
        val finalWidth = 380
        if (totalWidth > finalWidth) {
            val finalHeight = totalHeight / (totalWidth / finalWidth)
            val finalBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, false)
            val finalStream = ByteArrayOutputStream()
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, finalStream)
            AidlUtil.getInstance().printBitmap(finalBitmap)
        }
        Toast.makeText(mContext, finalWidth.toString(), Toast.LENGTH_SHORT).show()
    }

    // To generate BITMAP
    private fun getBitmapFromView(view: View, totalHeight: Int, totalWidth: Int): Bitmap {
        val returnedBitmap: Bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable: Drawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    private fun displayPriceDetails(type: Int) {
        val total = totalBill(mBillDetails, type)
        // Price Quantity
        subTotalTV.text = Constants.getCurrencyFormat(totalBill(mBillDetails, 1).toString())
        // Tax
        val tax = totalPrice * vat
        taxTV.text = Constants.getCurrencyFormat(tax.toString())
        // Total Price
        val totalPrice = total.plus(tax)
        totalBillTV.text = Constants.getCurrencyFormat(totalPrice.toString())
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
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
