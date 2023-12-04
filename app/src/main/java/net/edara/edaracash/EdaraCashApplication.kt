package net.edara.edaracash

import android.app.Application
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import com.pax.dal.IDAL
import com.pax.dal.IPrinter
import com.pax.neptunelite.api.NeptuneLiteUser
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class EdaraCashApplication : Application() {
    private var dal: IDAL? = null
    private var printer: IPrinter? = null
    private var instance: EdaraCashApplication? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getInstance(): EdaraCashApplication? {
        return instance
    }

    fun getPrinter(): IPrinter? {
        if (printer == null) {
            printer = getDal()!!.printer
        }
        return printer
    }

    private fun getDal(): IDAL? {
        if (dal == null) {
            try {
                dal = NeptuneLiteUser.getInstance().getDal(getInstance())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return dal
    }
}