package net.edara.edaracash.geidea


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.edara.edaracash.R


fun isGeideaInstalled(context: Context, uri: String): Boolean {
    val packageManger = context.packageManager
    try {
        packageManger.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }

}





