package net.edara.paxprinter

import android.app.Application
import com.pax.dal.IDAL
import com.pax.dal.IPrinter
import com.pax.neptunelite.api.NeptuneLiteUser

class PrintDemo : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: PrintDemo? = null
        var dal: IDAL =NeptuneLiteUser.getInstance().getDal(instance)



        val printer: IPrinter =   dal.printer

    }
}