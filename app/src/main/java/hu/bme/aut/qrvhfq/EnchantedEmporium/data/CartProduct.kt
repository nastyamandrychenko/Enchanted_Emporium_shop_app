package hu.bme.aut.qrvhfq.EnchantedEmporium.data

data class CartProduct(
    val product: Product,
    val quantity: Int
){
    constructor():this(Product(),1)
}
