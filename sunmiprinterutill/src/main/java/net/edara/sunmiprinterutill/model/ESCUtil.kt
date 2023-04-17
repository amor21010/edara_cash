package net.edara.sunmiprinterutill.model

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object ESCUtil {
    const val ESC: Byte = 0x1B // Escape
    const val FS: Byte = 0x1C // Text delimiter
    const val GS: Byte = 0x1D // Group separator
    const val DLE: Byte = 0x10 // data link escape
    const val EOT: Byte = 0x04 // End of transmission
    const val ENQ: Byte = 0x05 // Enquiry character
    const val SP: Byte = 0x20 // Spaces
    const val HT: Byte = 0x09 // Horizontal list
    const val LF: Byte = 0x0A //Print and wrap (horizontal orientation)
    const val CR: Byte = 0x0D // Home key
    const val FF: Byte =
        0x0C // Carriage control (print and return to the standard mode (in page mode))
    const val CAN: Byte = 0x18 // Canceled (cancel print data in page mode)
    fun init_printer(): ByteArray {
        val result = ByteArray(2)
        result[0] = ESC
        result[1] = 0x40
        return result
    }

    fun setPrinterDarkness(value: Int): ByteArray {
        val result = ByteArray(9)
        result[0] = GS
        result[1] = 40
        result[2] = 69
        result[3] = 4
        result[4] = 0
        result[5] = 5
        result[6] = 5
        result[7] = (value shr 8).toByte()
        result[8] = value.toByte()
        return result
    }

    fun getPrintQRCode(code: String, modulesize: Int, errorlevel: Int): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(setQRCodeSize(modulesize))
            buffer.write(setQRCodeErrorLevel(errorlevel))
            buffer.write(getQCodeBytes(code))
            buffer.write(getBytesForPrintQRCode(true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }

    fun getPrintDoubleQRCode(
        code1: String,
        code2: String,
        modulesize: Int,
        errorlevel: Int
    ): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(setQRCodeSize(modulesize))
            buffer.write(setQRCodeErrorLevel(errorlevel))
            buffer.write(getQCodeBytes(code1))
            buffer.write(getBytesForPrintQRCode(false))
            buffer.write(getQCodeBytes(code2))

            //加入横向间隔
            buffer.write(byteArrayOf(0x1B, 0x5C, 0x18, 0x00))
            buffer.write(getBytesForPrintQRCode(true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }

    fun getPrintQRCode2(data: String?, size: Int): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = 0x00
        val bytes2 = BytesUtil.getZXingQRCode(data, size)!!
        return BytesUtil.byteMerger(bytes1, bytes2)
    }

    fun getPrintBarCode(
        data: String,
        symbology: Int,
        height: Int,
        width: Int,
        textposition: Int
    ): ByteArray {
        var height = height
        var width = width
        var textposition = textposition
        if (symbology < 0 || symbology > 8) {
            return byteArrayOf(LF)
        }
        if (width < 2 || width > 6) {
            width = 2
        }
        if (textposition < 0 || textposition > 3) {
            textposition = 0
        }
        if (height < 1 || height > 255) {
            height = 162
        }
        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(
                byteArrayOf(
                    0x1D, 0x66, 0x01, 0x1D, 0x48, textposition.toByte(),
                    0x1D, 0x77, width.toByte(), 0x1D, 0x68, height.toByte(), 0x0A
                )
            )
            val barcode = data.toByteArray(charset("GB18030"))
            if (symbology == 8) {
                buffer.write(byteArrayOf(0x1D, 0x6B, 0x49, (barcode.size + 2).toByte(), 0x7B, 0x42))
            } else {
                buffer.write(
                    byteArrayOf(
                        0x1D,
                        0x6B,
                        (symbology + 0x41).toByte(),
                        barcode.size.toByte()
                    )
                )
            }
            buffer.write(barcode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }

    fun printBitmap(bitmap: Bitmap): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = 0x00
        val bytes2 = BytesUtil.getBytesFromBitMap(bitmap)
        return BytesUtil.byteMerger(bytes1, bytes2)
    }

    fun printBitmap(bytes: ByteArray): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = 0x00
        return BytesUtil.byteMerger(bytes1, bytes)
    }

    fun nextLine(lineNum: Int): ByteArray {
        val result = ByteArray(lineNum)
        for (i in 0 until lineNum) {
            result[i] = LF
        }
        return result
    }

    fun underlineWithOneDotWidthOn(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 45
        result[2] = 1
        return result
    }

    fun underlineWithTwoDotWidthOn(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 45
        result[2] = 2
        return result
    }

    fun underlineOff(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 45
        result[2] = 0
        return result
    }

    fun boldOn(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 69
        result[2] = 0xF
        return result
    }

    fun boldOff(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 69
        result[2] = 0
        return result
    }

    fun singleByte(): ByteArray {
        val result = ByteArray(2)
        result[0] = FS
        result[1] = 0x2E
        return result
    }

    fun singleByteOff(): ByteArray {
        val result = ByteArray(2)
        result[0] = FS
        result[1] = 0x26
        return result
    }

    fun setCodeSystemSingle(charset: Byte): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 0x74
        result[2] = charset
        return result
    }

    fun setCodeSystem(charset: Byte): ByteArray {
        val result = ByteArray(3)
        result[0] = FS
        result[1] = 0x43
        result[2] = charset
        return result
    }

    fun alignLeft(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 97
        result[2] = 0
        return result
    }

    fun alignCenter(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 97
        result[2] = 1
        return result
    }

    fun alignRight(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 97
        result[2] = 2
        return result
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////          private                /////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////
    private fun setQRCodeSize(modulesize: Int): ByteArray {
        val dtmp = ByteArray(8)
        dtmp[0] = GS
        dtmp[1] = 0x28
        dtmp[2] = 0x6B
        dtmp[3] = 0x03
        dtmp[4] = 0x00
        dtmp[5] = 0x31
        dtmp[6] = 0x43
        dtmp[7] = modulesize.toByte()
        return dtmp
    }

    private fun setQRCodeErrorLevel(errorlevel: Int): ByteArray {
        val dtmp = ByteArray(8)
        dtmp[0] = GS
        dtmp[1] = 0x28
        dtmp[2] = 0x6B
        dtmp[3] = 0x03
        dtmp[4] = 0x00
        dtmp[5] = 0x31
        dtmp[6] = 0x45
        dtmp[7] = (48 + errorlevel).toByte()
        return dtmp
    }

    private fun getBytesForPrintQRCode(single: Boolean): ByteArray {
        val dtmp: ByteArray
        if (single) {
            dtmp = ByteArray(9)
            dtmp[8] = 0x0A
        } else {
            dtmp = ByteArray(8)
        }
        dtmp[0] = 0x1D
        dtmp[1] = 0x28
        dtmp[2] = 0x6B
        dtmp[3] = 0x03
        dtmp[4] = 0x00
        dtmp[5] = 0x31
        dtmp[6] = 0x51
        dtmp[7] = 0x30
        return dtmp
    }

    private fun getQCodeBytes(code: String): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            val d = code.toByteArray(charset("GB18030"))
            var len = d.size + 3
            if (len > 7092) len = 7092
            buffer.write(0x1D.toByte().toInt())
            buffer.write(0x28.toByte().toInt())
            buffer.write(0x6B.toByte().toInt())
            buffer.write(len.toByte().toInt())
            buffer.write((len shr 8).toByte().toInt())
            buffer.write(0x31.toByte().toInt())
            buffer.write(0x50.toByte().toInt())
            buffer.write(0x30.toByte().toInt())
            var i = 0
            while (i < d.size && i < len) {
                buffer.write(d[i].toInt())
                i++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }
}