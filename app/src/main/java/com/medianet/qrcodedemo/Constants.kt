package com.medianet.qrcodedemo

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class Constants {
    companion object {
        // Convert to double
        private fun convertToDouble(initialString: String?): Double {
            return if (initialString != null && !initialString.isEmpty()) {
                java.lang.Double.parseDouble(initialString)
            } else 0.00
        }

        // Currency Format IN Лв
        fun getCurrencyFormat(currency: String?): String {
            val currencyValue = convertToDouble(currency)
            val locale = Locale("en", "UZS")
            val decimalFormat = DecimalFormat.getCurrencyInstance(locale) as DecimalFormat
            val dfs = DecimalFormatSymbols.getInstance(locale)
            dfs.currencySymbol = "Лв "
            //dfs.currencySymbol = ""
            decimalFormat.decimalFormatSymbols = dfs
            return decimalFormat.format(currencyValue)
        }

        fun twoDecimals(price: String?): String {
            return ("%.2f".format(convertToDouble(price)))
            //return convertToDouble(price).toString()
        }
    }
}