package net.edara.sunmiprinterutill.model

interface PrinterCallback {
    fun getResult(): String?
    fun onReturnString(result: String?)
}
