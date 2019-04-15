package com.medianet.qrcodedemo.NetworkInterceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Created by JANI on 27-02-2018.
 */
class ConnectivityInterceptor(private val mContext: Context): Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        if (!NetworkUtil.isOnline(mContext)) {
            throw NoConnectivityException()
        }
        val builder: Request.Builder = chain!!.request().newBuilder()
        return chain.proceed(builder.build())
    }

}