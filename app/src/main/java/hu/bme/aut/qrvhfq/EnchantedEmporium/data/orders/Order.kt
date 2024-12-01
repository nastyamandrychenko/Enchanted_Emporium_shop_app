package hu.bme.aut.qrvhfq.EnchantedEmporium.data.orders

import hu.bme.aut.qrvhfq.EnchantedEmporium.data.Address
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.CartProduct

data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address

)
