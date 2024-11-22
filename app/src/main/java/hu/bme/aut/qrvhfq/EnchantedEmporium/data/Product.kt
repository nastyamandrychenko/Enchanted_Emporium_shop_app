package hu.bme.aut.qrvhfq.EnchantedEmporium.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val description: String? = null,
    val images: List<String>
) : Parcelable{
    constructor(): this("0", "", "",0f, "", images= emptyList())
}
