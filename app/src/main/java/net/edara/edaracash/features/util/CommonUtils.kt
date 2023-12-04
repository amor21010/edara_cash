package net.edara.edaracash.features.util

import android.content.pm.PackageManager
import android.view.View
import androidx.fragment.app.Fragment

object CommonUtils {
    fun Fragment.isFawryPOS(): Boolean {
        val packageName = "com.fawry.retailer"

        return try {
            val packageInfo = requireActivity().applicationContext.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            packageInfo != null
        } catch (e: PackageManager.NameNotFoundException) {
            // Package is not installed
            false
        }
    }

    fun View.setVisibility(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE
        else
            View.GONE
    }
}