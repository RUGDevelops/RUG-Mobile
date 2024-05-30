package eu.virtusdevelops.datalib.models.deliveryPackage

data class Recipient(
    val firstname: String,
    val lastname: String,
    val email: String,

    val street: String,
    val houseNumber: String,
    val city: String,
    val postNumber: String,
    val country: String,
)
