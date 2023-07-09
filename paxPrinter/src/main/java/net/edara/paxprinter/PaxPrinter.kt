package net.edara.paxprinter

import android.graphics.Bitmap
import android.util.Log
import com.pax.dal.IPrinter
import com.pax.dal.exceptions.PrinterDevException

class PaxPrinter private constructor() {
    private val printer: IPrinter = PrintDemo.printer
    private val TAG = "PaxPrinter"

    fun init() {
        try {
            printer.init()
            printer.setGray(4)
            Log.i(TAG, "init")
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            Log.i(TAG, "init: $e")
        }
    }

    val status: Int?
        get() = try {
            val status = printer.status
            Log.i(TAG, "getStatus")
            status
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            Log.i(TAG, "getStatus$e")
            -1
        }

    fun printBitmap(bitmap: Bitmap?) {
        try {
            printer.printBitmap(bitmap)
            Log.i(TAG, "printBitmap")
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            Log.i(TAG, "printBitmap$e")
        }
    }

    fun start(): String {
        return try {
            val res = printer.start()
            Log.i(TAG, "start")
            statusCode2Str(res)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            Log.i(TAG, "start$e")
            ""
        }
    }

    private fun statusCode2Str(status: Int?): String {
        var res = ""
        when (status) {
            0 -> res = "Success"
            1 -> res = "Printer is busy "
            2 -> res = "Out of paper "
            3 -> res = "The format of print data packet error "
            4 -> res = "Printer malfunctions "
            8 -> res = "Printer over heats "
            9 -> res = "Printer voltage is too low"
            240 -> res = "Printing is unfinished "
            252 -> res = " The printer has not installed font library "
            254 -> res = "Data package is too long "
            else -> {}
        }
        return res
    }

    companion object {
        private var paxPrinter: PaxPrinter? = null
        val instance: PaxPrinter?
            get() {
                if (paxPrinter == null) {
                    paxPrinter = PaxPrinter()
                }
                return paxPrinter
            }
    }
}