package net.edara.edaracash.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.print.PrintResponse


@Parcelize
data class InvoiceBuilder(
    val serviceList: List<GetAllServiceResonse.Data.Service>,
    val extrasDto: ExtrasDto,

) : Parcelable

@Parcelize
data class ExtrasDto(
    val extraCharge: Double = 0.0,
    val tax: Double = 0.0,
    val discount: Double = 0.0
) : Parcelable

