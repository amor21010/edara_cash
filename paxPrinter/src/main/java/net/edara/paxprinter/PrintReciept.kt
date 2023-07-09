package net.edara.paxprinter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.pax.gl.page.IPage
import com.pax.gl.page.IPage.EAlign
import com.pax.gl.page.PaxGLPage
import org.json.JSONArray
import org.json.JSONException
import java.util.Locale

class PrintReceipt(private val context: Context) {
    private var bitmap: Bitmap? = null
    private val handler = Handler()
    private var multiInvoices: JSONArray? = null
    private var logoBMp: Bitmap? = null
    private var index = 0
    fun printReceipt(invoiceData: String?) {
        logoBMp = BitmapFactory.decodeResource(context.resources, R.drawable.pax_logo_normal)
        if (invoiceData == null || invoiceData.isEmpty()) {
            return
        }
        try {
            Log.i("Data", invoiceData)
            multiInvoices = JSONArray(invoiceData)
            index = 0
            generateInvoice()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun generateInvoice() {
        if (index >= multiInvoices!!.length()) {
            return
        }
        val paxGLPage = PaxGLPage.getInstance(context)
        val page = paxGLPage.createPage()
        page.adjustLineSpace(-9)
        page.addLine().addUnit(logoBMp, EAlign.CENTER)
        page.addLine().addUnit("\n", 7)
        try {
            val invoice = multiInvoices!!.getJSONArray(index)
            Log.i("Data", invoice.toString())
            for (j in 0 until invoice.length()) {
                Log.i("Data", invoice.optJSONObject(j).toString())
                val text = invoice.optJSONObject(j).getString("text")
                var align: EAlign
                align = when (invoice.optJSONObject(j).optString("dir")) {
                    "right" -> EAlign.RIGHT
                    "left" -> EAlign.LEFT
                    else -> EAlign.CENTER
                }
                val textSize = invoice.optJSONObject(j).getString("size").toInt()
                var style: Int
                style = when (invoice.optJSONObject(j).optString("style")) {
                    "bold" -> IPage.ILine.IUnit.TEXT_STYLE_BOLD
                    "italic" -> IPage.ILine.IUnit.TEXT_STYLE_ITALIC
                    "underline" -> IPage.ILine.IUnit.TEXT_STYLE_UNDERLINE
                    else -> IPage.ILine.IUnit.TEXT_STYLE_NORMAL
                }
                page.addLine().addUnit(text, textSize, align, style)
                page.addLine().addUnit("\n", 5)
            }
            page.addLine().addUnit("\n", 30)
            bitmap = paxGLPage.pageToBitmap(page, 384)
            printReceipt()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun printReceipt() {
        Log.i(
            "printReceipt",
            """
                ${Build.MANUFACTURER.uppercase(Locale.getDefault())}
                ${Build.BRAND.uppercase(Locale.getDefault())}
                ${Build.DEVICE.uppercase(Locale.getDefault())}
                ${Build.MODEL.uppercase(Locale.getDefault())}
                ${Build.PRODUCT.uppercase(Locale.getDefault())}
                
                """.trimIndent()
        )
        if ("PAX".equals(Build.BRAND, ignoreCase = true) || "PAX".equals(
                Build.MANUFACTURER,
                ignoreCase = true
            )
        ) { //case of pax device
            Thread {
                PaxPrinter.instance?.init()
                PaxPrinter.instance?.printBitmap(bitmap)
                PaxPrinter.instance?.start()?.let { onShowMessage(it) }
            }.start()
        } else { //for other devices use bluetooth print
            Log.i("printReceipt", "not pax\n")
        }
    }

    private fun onShowMessage(message: String) {
        handler.post {
            if (message != "Success") {
                val alertDialog =
                    AlertDialog.Builder(
                        context
                    )
                alertDialog.setTitle(PrintDemo.instance?.getString(R.string.oops))
                alertDialog.setMessage(message)
                alertDialog.setPositiveButton(
                    PrintDemo.instance?.resources?.getString(R.string.try_again)
                ) { dialog, which ->
                    printReceipt()
                    dialog.cancel()
                }
                alertDialog.setNegativeButton(
                    PrintDemo.instance?.resources?.getString(R.string.cancel)
                ) { dialog, which ->
                    dialog.cancel()
                    index++
                    generateInvoice()
                }
                alertDialog.show()
            } else {
                index++
                generateInvoice()
            }
        }
    }
}