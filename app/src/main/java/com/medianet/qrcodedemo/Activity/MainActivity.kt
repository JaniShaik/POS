package com.medianet.qrcodedemo.Activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.medianet.qrcodedemo.Adapter.DashboardAdapter
import com.medianet.qrcodedemo.R
import kotlinx.android.synthetic.main.activity_main.*
import pl.tajchert.nammu.Nammu
import pl.tajchert.nammu.PermissionCallback
import android.R.attr.data
import android.graphics.Typeface
import android.support.v4.app.NotificationCompat.getExtras
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Toast
import com.medianet.qrcodedemo.AidlUtil
import com.medianet.qrcodedemo.BytesUtil
import com.medianet.qrcodedemo.Constants
import com.medianet.qrcodedemo.Constants.Companion.twoDecimals
import com.medianet.qrcodedemo.Retrofit.DataClass.ProductDetails

import woyou.aidlservice.jiuiv5.ICallback
import woyou.aidlservice.jiuiv5.IWoyouService
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), DashboardAdapter.UserClickCallbacks {
    // Context
    val mContext: Context = this
    // RecyclerView....
    var dashboardAdapter: DashboardAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    // Menu Item....
    var gridMenu: MenuItem? = null
    var listMenu: MenuItem? = null
    // List Menu
    private val menuNames = ArrayList<String>()
    private val menuIcons = ArrayList<Int>()
    // Bill Details
    var mBillDetails = ArrayList<ProductDetails>()
    // Permissions
    private var permissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_NETWORK_STATE)
    //
    private val START_SCAN = 2
    //
    private var billString = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //
        val productDetails = ProductDetails("1", "PRODUCT 1", "MODEL 1", "613", "CODE", true, 1, 613456789.00)
        mBillDetails.add(productDetails)
        // Menu's
        menuDetails()
        // Permissions
        Nammu.init(applicationContext)
        Nammu.askForPermission(this@MainActivity, permissions, permissionContactsCallback)
        // Recycler View....
        dashboardAdapter = DashboardAdapter(mContext, menuIcons, menuNames, this, 2)
        layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = dashboardAdapter
        dashboardAdapter!!.notifyDataSetChanged()
        // Nav bar
        supportActionBar?.title = "User Name"
        //
        //prepareString()
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
            //var price = Constants.getCurrencyFormat(product.totalPrice.toString())
            var price = twoDecimals(product.totalPrice.toString())
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
                    qty += " "
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
        billString.append("*******************************\n")
        var subTotal = "Sub Total: " + twoDecimals("200")
        if (subTotal.length < 30) {
            for (i in subTotal.length..30) {
                subTotal = " " + subTotal
            }
        }
        billString.append(subTotal + "\n")
        // Tax
        var tax = "Tax(18%): " + twoDecimals("18")
        if (tax.length < 30) {
            for (i in tax.length..30) {
                tax = " " + tax
            }
        }
        billString.append(tax + "\n")
        billString.append("*******************************\n")
        // Total
        val totalBuilder = StringBuilder()
        val total = "Grand Total: " + twoDecimals("218")
        totalBuilder.append(total + "\n")
        // Footer
        val footerBuilder = StringBuilder()
        footerBuilder.append("*******************************\n")
        //footerBuilder.append("-------------------------------\n")
        footerBuilder.append("Have a nice day\n")
        footerBuilder.append("Thank You | Visit Again\n\n\n\n")
        //
        //val format = byteArrayOf(27, 33, 0)
        //format[2] = (0x8 or format[2].toInt()) as Byte
        //
        val contentData = billString.toString().toByteArray()
        val footerData = footerBuilder.toString().toByteArray()
        //
        AidlUtil.getInstance().prepareDataToPrint(heading.toString(), contentData, totalBuilder.toString(), footerData)
        //AidlUtil.getInstance().sendRawData(stringData)
    }

    // Date
    private fun dateFormat(): String {
        val cal = Calendar.getInstance()
        val date = cal.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss ")
        val formattedDate = dateFormat.format(date)
        return formattedDate
    }

    // Menu Details
    private fun menuDetails() {
        // Menu Names
        menuNames.add("Bill")
        menuNames.add("Accept")
        menuNames.add("Product")
        menuNames.add("Payment")
        menuNames.add("Register")
        // Menu Icons
        menuIcons.add(R.drawable.bill)
        menuIcons.add(R.drawable.accept)
        menuIcons.add(R.drawable.activate)
        menuIcons.add(R.drawable.payment)
        menuIcons.add(R.drawable.register)
    }

    // On Menu Click
    override fun onUserClick(position: Int) {
        val menuName = menuNames[position]
        when (menuName) {
            "Bill" -> {
                val intent = Intent(mContext, ScanActivity::class.java)
                startActivity(intent)
            }
            "Accept" -> {
                val intent = Intent("com.summi.scan")
                intent.setPackage("com.sunmi.sunmiqrcodescanner")
                intent.putExtra("CURRENT_PPI", 0X0003);//The current preview resolution ,PPI_1920_1080 = 0X0001;PPI_1280_720 = 0X0002;PPI_BEST = 0X0003;

                intent.putExtra("PLAY_SOUND", true);// Prompt tone after scanning  ,default true

                intent.putExtra("PLAY_VIBRATE", false);//vibrate after scanning,default false,only support M1 right now.

                intent.putExtra("IDENTIFY_INVERSE_QR_CODE", true);//Whether to identify inverse code

                intent.putExtra("IDENTIFY_MORE_CODE", false);// Whether to identify several code，default false

                intent.putExtra("IS_SHOW_SETTING", true);// Wether display set up button  at the top-right corner，default true

                intent.putExtra("IS_SHOW_ALBUM", true);// Wether display album，default true
                startActivityForResult(intent, START_SCAN);
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_SCAN && data != null) {
            val bundle = data.extras
            val result = bundle
                    .getSerializable("data") as ArrayList<HashMap<String, String>>

            val it = result.iterator()

            while (it.hasNext()) {
                val hashMap = it.next()

                Log.i("sunmi", hashMap["TYPE"])//this is the type of the code
                Log.i("sunmi", hashMap["VALUE"])//this is the result of the code

            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Permission callback
     */
    private val permissionContactsCallback: PermissionCallback = object : PermissionCallback {
        override fun permissionGranted() {

        }

        override fun permissionRefused() {

        }
    }

    /**
     * Permission check
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }
}
