@file:Suppress("DEPRECATION")

package net.edara.edaracash.paymentMethods.geidea


import android.content.Context
import android.content.pm.PackageManager


fun isGeideaInstalled(context: Context, uri: String): Boolean {
    val packageManger = context.packageManager
    return try {
        packageManger.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

}





