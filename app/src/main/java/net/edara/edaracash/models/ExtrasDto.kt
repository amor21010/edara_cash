package net.edara.edaracash.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ExtrasDto(
    val extraCharge: Double = 0.0,
    val tax: Double = 0.0,
    val discount: Double = 0.0
) : Parcelable
