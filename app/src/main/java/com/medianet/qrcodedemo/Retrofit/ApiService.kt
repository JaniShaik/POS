package com.medianet.qrcodedemo.Retrofit

import android.content.Context
import com.medianet.qrcodedemo.NetworkInterceptor.ConnectivityInterceptor
import com.medianet.qrcodedemo.Retrofit.DataClass.Product
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface ApiService {

    // Product Details
    @GET("qrcode.php")
    fun getProductDetails(): Call<Product>

    /**
     * Companion object to create the ApiService
     */
    companion object Factory {
        fun create(url: String, context: Context): ApiService {
            val client = OkHttpClient.Builder()
                    .readTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .addInterceptor(ConnectivityInterceptor(context))
                    .build()
            //val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(url)
                    .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}