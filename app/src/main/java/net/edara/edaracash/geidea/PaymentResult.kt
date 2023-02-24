package net.edara.edaracash.geidea

import org.json.JSONObject

data class PaymentResult(
    val transactionRefNo: String,
    val cardScheme: String,
    val maskedCardNo: String,
    val stan: String,
    val transactionMode: String,
    val appVersion: String,
    val responseCode: String,
    val acquirerID: String,
    val terminalID: String,
    val amount: String,
    val authCode: String,
    val responseMessage: String
) {



    companion object {
        fun fromJson(result: String): PaymentResult {
            val jsonObject = JSONObject(result)

            return PaymentResult(
                transactionRefNo = jsonObject.optString("rrn"),
                cardScheme = jsonObject.optString("card_scheme"),
                maskedCardNo = jsonObject.optString("pan"),
                stan = jsonObject.optString("stan"),
                transactionMode = jsonObject.optString("entry_mode"),
                appVersion = jsonObject.optString("app_version"),
                responseCode = jsonObject.optString("resp_code"),
                acquirerID = jsonObject.optString("acq_id"),
                terminalID = jsonObject.optString("terminal_id"),
                amount = jsonObject.optString("amount"),
                authCode = jsonObject.optString("auth_code"),
                responseMessage = jsonObject.optString("res_desc")
            )
        }
    }


}
