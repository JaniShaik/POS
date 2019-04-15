package com.medianet.qrcodedemo.Retrofit.DataClass

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
        @SerializedName("status") val status: Boolean,
        @SerializedName("error") val error: String?,
        @SerializedName("response") val response: ProductDetails
) : Serializable

data class ProductDetails(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("model") val model: String,
        @SerializedName("price") var price: String,
        @SerializedName("code") val code: String,
        @SerializedName("status") val status: Boolean,
        @SerializedName("qty") var qty: Int,
        @SerializedName("total_price") var totalPrice: Double
) : Serializable