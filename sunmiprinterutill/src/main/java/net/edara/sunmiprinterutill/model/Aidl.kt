
package net.edara.sunmiprinterutill.model
/*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.IBinder
import android.os.RemoteException
import android.widget.Toast
import com.edara.sunmiprinterutill.R
import woyou.aidlservice.jiuiv5.ICallback
import woyou.aidlservice.jiuiv5.IWoyouService




class AidlUtil private constructor() {



    private var woyouService: IWoyouService? = null
    private var context: Context? = null


    private val connService: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            woyouService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            woyouService = IWoyouService.Stub.asInterface(service)
        }
    }
    private val darkness = intArrayOf(
        0x0600, 0x0500, 0x0400, 0x0300, 0x0200, 0x0100, 0,
        0xffff, 0xfeff, 0xfdff, 0xfcff, 0xfbff, 0xfaff
    )

    fun connectPrinterService(context: Context) {
        this.context = context.applicationContext
        val intent = Intent()
        intent.setPackage(SERVICE_PACKAGE)
        intent.setAction(SERVICE_ACTION)
        context.applicationContext.startService(intent)
        context.applicationContext.bindService(intent, connService, Context.BIND_AUTO_CREATE)
    }

    fun disconnectPrinterService(context: Context) {
        if (woyouService != null) {
            context.applicationContext.unbindService(connService)
            woyouService = null
        }
    }

    val isConnect: Boolean
        get() = woyouService != null

    fun generateCB(printerCallback: PrinterCallback): ICallback {
        return object : ICallback.Stub() {
            @Throws(RemoteException::class)
            override fun onRunResult(isSuccess: Boolean) {
            }

            @Throws(RemoteException::class)
            override fun onReturnString(result: String) {
                printerCallback.onReturnString(result)
            }

            @Throws(RemoteException::class)
            override fun onRaiseException(code: Int, msg: String) {
            }
        }
    }

    fun setDarkness(index: Int) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show()
            return
        }
        val k = darkness[index]
        try {
            woyouService!!.sendRAWData(ESCUtil.setPrinterDarkness(k), null)
            woyouService!!.printerSelfChecking(null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun getPrinterInfo(printerCallback: PrinterCallback): List<String>? {
        if (woyouService == null) {
            Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show()
            return null
        }
        val info: MutableList<String> = ArrayList()
        try {
            woyouService!!.getPrintedLength(generateCB(printerCallback))
            info.add(woyouService!!.printerSerialNo)
            info.add(woyouService!!.printerModal)
            info.add(woyouService!!.printerVersion)
            info.add(printerCallback.result)
            info.add("")
            //info.add(woyouService.getServiceVersion());
            val packageManager = context!!.packageManager
            try {
                val packageInfo: PackageInfo = packageManager.getPackageInfo(
                    SERVICE_PACKAGE, 0)
                if (packageInfo != null) {
                    info.add(packageInfo.versionName)
                    info.add(packageInfo.versionCode.toString() + "")
                } else {
                    info.add("")
                    info.add("")
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        return info
    }

    fun initPrinter() {
        if (woyouService == null) {
            Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.printerInit(null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun printQr(data: String?, modulesize: Int, errorlevel: Int) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.setAlignment(1, null)
            woyouService!!.printQRCode(data, modulesize, errorlevel, null)
            woyouService!!.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun printBarCode(data: String?, symbology: Int, height: Int, width: Int, textposition: Int) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.printBarCode(data, symbology, height, width, textposition, null)
            woyouService!!.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun printText(
        content: String?,
        size: Float,
        isBold: Boolean,
        isUnderLine: Boolean,
        lineBreak: Int
    ) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show()
            return
        }
        try {
            if (isBold) {
                woyouService!!.sendRAWData(ESCUtil.boldOn(), null)
            } else {
                woyouService!!.sendRAWData(ESCUtil.boldOff(), null)
            }
            if (isUnderLine) {
                woyouService!!.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null)
            } else {
                woyouService!!.sendRAWData(ESCUtil.underlineOff(), null)
            }
            woyouService!!.printTextWithFont(content, null, size, null)
            woyouService!!.lineWrap(lineBreak, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun printBitmap(bitmap: Bitmap?) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.setAlignment(1, null)
            woyouService!!.printBitmap(bitmap, null)
            woyouService!!.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    //    public void printTable(LinkedList<TableItem> list) {
    //        if (woyouService == null) {
    //            Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show();
    //            return;
    //        }
    //
    //        try {
    //            for (TableItem tableItem : list) {
    //                Log.i("kaltin", "printTable: " + tableItem.getText()[0] + tableItem.getText()[1] + tableItem.getText()[2]);
    //                woyouService.printColumnsString(tableItem.getText(), tableItem.getWidth(), tableItem.getAlign(), null);
    //            }
    //            woyouService.lineWrap(3, null);
    //        } catch (RemoteException e) {
    //            e.printStackTrace();
    //        }
    //    }
    fun print3Line() {
        if (woyouService == null) {
            Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun sendRawData(data: ByteArray?) {
        if (woyouService == null) {
            Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show()
            return
        }
        try {
            woyouService!!.sendRAWData(data, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    companion object {
        private val SERVICE_PACKAGE = "woyou.aidlservice.jiuiv5"
        private val SERVICE_ACTION = "woyou.aidlservice.jiuiv5.IWoyouService"
        val instance = AidlUtil()
    }
}*/
