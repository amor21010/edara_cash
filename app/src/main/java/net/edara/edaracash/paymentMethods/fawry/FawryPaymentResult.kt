package net.edara.edaracash.paymentMethods.fawry


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.json.JSONObject


@Parcelize
data class FawryPaymentResult(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("header")
    val header: Header?
) : Parcelable {
    @Parcelize
    data class Body(
        @SerializedName("amount")
        val amount: Double?,
        @SerializedName("balance")
        val balance: Double?,
        @SerializedName("btc")
        val btc: Int?,
        @SerializedName("clientTerminalSequenceID")
        val clientTerminalSequenceID: String?,
        @SerializedName("currency")
        val currency: String?,
        @SerializedName("discount")
        val discount: Discount?,
        @SerializedName("fawryReference")
        val fawryReference: String?,
        @SerializedName("fees")
        val fees: Double?,
        @SerializedName("operationModel")
        val operationModel: String?,
        @SerializedName("paymentOption")
        val paymentOption: String?,
        @SerializedName("printReceipt")
        val printReceipt: Boolean?,
        @SerializedName("receiptInfo")
        val receiptInfo: ReceiptInfo?,
        @SerializedName("signature")
        val signature: String?,
        @SerializedName("transactionType")
        val transactionType: String?
    ) : Parcelable {
        @Parcelize
        data class Discount(
            @SerializedName("amount")
            val amount: Double?
        ) : Parcelable {
            companion object {
                @JvmStatic
                fun buildFromJson(jsonObject: JSONObject?): Discount? {

                    jsonObject?.run {
                        return Discount(
                            optDouble("amount")
                        )
                    }
                    return null
                }
            }
        }

        @Parcelize
        data class ReceiptInfo(
            @SerializedName("acquirerBankId")
            val acquirerBankId: String?,
            @SerializedName("authId")
            val authId: String?,
            @SerializedName("cardInfo")
            val cardInfo: CardInfo?,
            @SerializedName("effDt")
            val effDt: String?,
            @SerializedName("installmentPlan")
            val installmentPlan: InstallmentPlan?,
            @SerializedName("merchantId")
            val merchantId: String?,
            @SerializedName("pinMode")
            val pinMode: String?,
            @SerializedName("receiptNumber")
            val receiptNumber: String?,
            @SerializedName("rrn")
            val rrn: String?,
            @SerializedName("terminalId")
            val terminalId: String?
        ) : Parcelable {
            @Parcelize
            data class CardInfo(
                @SerializedName("appID")
                val appID: String?,
                @SerializedName("appName")
                val appName: String?,
                @SerializedName("cardAcctId")
                val cardAcctId: String?,
                @SerializedName("cardHolderName")
                val cardHolderName: String?,
                @SerializedName("issuerBankId")
                val issuerBankId: String?
            ) : Parcelable {
                companion object {
                    @JvmStatic
                    fun buildFromJson(jsonObject: JSONObject?): CardInfo? {

                        jsonObject?.run {
                            return CardInfo(
                                optString("appID"),
                                optString("appName"),
                                optString("cardAcctId"),
                                optString("cardHolderName"),
                                optString("issuerBankId")
                            )
                        }
                        return null
                    }
                }
            }

            @Parcelize
            data class InstallmentPlan(
                @SerializedName("code")
                val code: String?,
                @SerializedName("merchant")
                val merchant: Merchant?,
                @SerializedName("monthlyAmount")
                val monthlyAmount: Double?,
                @SerializedName("period")
                val period: String?,
                @SerializedName("rate")
                val rate: String?,
                @SerializedName("totalAmount")
                val totalAmount: Double?
            ) : Parcelable {
                @Parcelize
                data class Merchant(
                    @SerializedName("acquirerBankId")
                    val acquirerBankId: String?,
                    @SerializedName("merchantId")
                    val merchantId: String?,
                    @SerializedName("terminalId")
                    val terminalId: String?
                ) : Parcelable {
                    companion object {
                        @JvmStatic
                        fun buildFromJson(jsonObject: JSONObject?): Merchant? {

                            jsonObject?.run {
                                return Merchant(
                                    optString("acquirerBankId"),
                                    optString("merchantId"),
                                    optString("terminalId")
                                )
                            }
                            return null
                        }
                    }
                }

                companion object {
                    @JvmStatic
                    fun buildFromJson(jsonObject: JSONObject?): InstallmentPlan? {

                        jsonObject?.run {
                            return InstallmentPlan(
                                optString("code"),
                                Merchant.buildFromJson(optJSONObject("merchant")),
                                optDouble("monthlyAmount"),
                                optString("period"),
                                optString("rate"),
                                optDouble("totalAmount")
                            )
                        }
                        return null
                    }
                }
            }

            companion object {
                @JvmStatic
                fun buildFromJson(jsonObject: JSONObject?): ReceiptInfo? {

                    jsonObject?.run {
                        return ReceiptInfo(
                            optString("acquirerBankId"),
                            optString("authId"),
                            CardInfo.buildFromJson(optJSONObject("cardInfo")),
                            optString("effDt"),
                            InstallmentPlan.buildFromJson(optJSONObject("installmentPlan")),
                            optString("merchantId"),
                            optString("pinMode"),
                            optString("receiptNumber"),
                            optString("rrn"),
                            optString("terminalId")
                        )
                    }
                    return null
                }
            }
        }

        companion object {
            @JvmStatic
            fun buildFromJson(jsonObject: JSONObject?): Body? {

                jsonObject?.run {
                    return Body(
                        optDouble("amount"),
                        optDouble("balance"),
                        optInt("btc"),
                        optString("clientTerminalSequenceID"),
                        optString("currency"),
                        Discount.buildFromJson(optJSONObject("discount")),
                        optString("fawryReference"),
                        optDouble("fees"),
                        optString("operationModel"),
                        optString("paymentOption"),
                        optBoolean("printReceipt"),
                        ReceiptInfo.buildFromJson(optJSONObject("receiptInfo")),
                        optString("signature"),
                        optString("transactionType")
                    )
                }
                return null
            }
        }
    }

    @Parcelize
    data class Header(
        @SerializedName("messageCode")
        val messageCode: String?,
        @SerializedName("password")
        val password: String?,
        @SerializedName("requestUuid")
        val requestUuid: String?,
        @SerializedName("serverTimestamp")
        val serverTimestamp: String?,
        @SerializedName("status")
        val status: Status?,
        @SerializedName("userName")
        val userName: String?
    ) : Parcelable {
        @Parcelize
        data class Status(
            @SerializedName("hostStatusCode")
            val hostStatusCode: Int?,
            @SerializedName("hostStatusDesc")
            val hostStatusDesc: String?,
            @SerializedName("statusCode")
            val statusCode: Int?,
            @SerializedName("statusDesc")
            val statusDesc: String?
        ) : Parcelable {
            companion object {
                @JvmStatic
                fun buildFromJson(jsonObject: JSONObject?): Status? {

                    jsonObject?.run {
                        return Status(
                            optInt("hostStatusCode"),
                            optString("hostStatusDesc"),
                            optInt("statusCode"),
                            optString("statusDesc")
                        )
                    }
                    return null
                }
            }
        }

        companion object {
            @JvmStatic
            fun buildFromJson(jsonObject: JSONObject?): Header? {

                jsonObject?.run {
                    return Header(
                        optString("messageCode"),
                        optString("password"),
                        optString("requestUuid"),
                        optString("serverTimestamp"),
                        Status.buildFromJson(optJSONObject("status")),
                        optString("userName")
                    )
                }
                return null
            }
        }
    }

    companion object {
        @JvmStatic
        fun buildFromJson(result: String): FawryPaymentResult {
            val jsonObject = JSONObject(result)
            jsonObject.run {
                return FawryPaymentResult(
                    Body.buildFromJson(optJSONObject("body")),
                    Header.buildFromJson(optJSONObject("header"))
                )
            }
        }
    }
}