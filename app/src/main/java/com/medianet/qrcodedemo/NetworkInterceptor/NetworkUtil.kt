package com.medianet.qrcodedemo.NetworkInterceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by personal on 27-02-2018.
 */
class NetworkUtil {
    companion object {
        fun isOnline(context: Context): Boolean {
            val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo: NetworkInfo? = cm.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}