package net.edara.sunmiprinterutill.trans

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class TransBean : Parcelable {
    var type: Byte = 0
    var text: String? = ""
    private var data: ByteArray? = null
    private var datalength = 0

    constructor() {
        type = 0
        data = null
        text = ""
        datalength = 0
    }

    fun getData(): ByteArray? {
        return data
    }

    fun setData(data: ByteArray?) {
        if (data != null) {
            datalength = data.size
            this.data = ByteArray(datalength)
            System.arraycopy(data, 0, this.data, 0, datalength)
        }
    }

    constructor(source: Parcel) {
        type = source.readByte()
        datalength = source.readInt()
        text = source.readString()
        if (datalength > 0) {
            data = ByteArray(datalength)
            source.readByteArray(data!!)
        }
    }

    constructor(type: Byte, text: String?, data: ByteArray?) {
        this.type = type
        this.text = text
        if (data != null) {
            datalength = data.size
            this.data = ByteArray(datalength)
            System.arraycopy(data, 0, this.data, 0, datalength)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte(type)
        dest.writeInt(datalength)
        dest.writeString(text)
        if (data != null) {
            dest.writeByteArray(data)
        }
    }

    companion object {
        @JvmField
        var CREATOR : Creator<TransBean> = object : Creator<TransBean> {
            override fun createFromParcel(source: Parcel): TransBean {
                return TransBean(source)
            }

            override fun newArray(size: Int): Array<TransBean?> {
                return arrayOfNulls(size)
            }
        }
    }
}