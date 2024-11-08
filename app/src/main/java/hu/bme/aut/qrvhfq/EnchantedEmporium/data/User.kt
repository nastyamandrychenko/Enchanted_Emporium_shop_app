package hu.bme.aut.qrvhfq.EnchantedEmporium.data

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    var imagePatg: String = ""



){
    constructor():this("","","","")
}
