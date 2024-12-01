package hu.bme.aut.qrvhfq.EnchantedEmporium.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val country: String = "",
    val fullName: String = "",
    val street: String = "",
    val phone: String = "",
    val city: String = "",
    val postalCode: String = ""
): Parcelable