package hu.bme.aut.qrvhfq.EnchantedEmporium.data

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val description: String? = null,
    val images: List<String>
)
