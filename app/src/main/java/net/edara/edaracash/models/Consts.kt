package net.edara.edaracash.models

import android.content.pm.PackageManager
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment

object Consts {
 val USER_TOKEN = stringPreferencesKey("Edara_Cash_Token")
 val FAWRY_USERNAME=stringPreferencesKey("Fawry.FPOS.UserName")
 val FAWRY_Password=stringPreferencesKey("Fawry.FPOS.Password")
 val IS_PAYMENT_FIRST_TIME = booleanPreferencesKey("IS_PAYMENT_FIRST_TIME")
 const val PRIVET_SERVICE_PERMISSION=("PSRequest.FPOS.Default")

 const val INSURANCE_PERMISSION=("INRequest.FPOS.Default")
 const val INSURANCE_PAY_PERMISSION=("INRequest.FPOS.Print")
 const val INSURANCE_PRINT_PERMISSION=("INRequest.FPOS.Pay")
 const val PRIVET_SERVICE_PRINT_PERMISSION=("PSRequest.FPOS.Print")
 const val PRIVET_SERVICE_PAY_PERMISSION=("PSRequest.FPOS.Pay")
 const val ALLOWED_ROLE=("POS-Financial")


}

