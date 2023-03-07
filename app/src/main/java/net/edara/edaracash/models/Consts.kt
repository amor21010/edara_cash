package net.edara.edaracash.models

import androidx.datastore.preferences.core.stringPreferencesKey

object Consts {
 val USER_TOKEN = stringPreferencesKey("Edara_Cash_Token")
 const val PRIVET_SERVICE_PERMISSION=("PSRequest.FPOS.Default")
 const val INSURANCE_PERMISSION=("INRequest.TPOS.Default")
 const val PRINT_PERMISSION=("PSRequest.FPOS.Print")
 const val PAY_PERMISSION=("PSRequest.FPOS.Pay")
}