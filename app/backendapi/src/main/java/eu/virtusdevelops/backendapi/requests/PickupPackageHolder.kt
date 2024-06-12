package eu.virtusdevelops.backendapi.requests

data class PickupPackageHolder(
    val id: Int,
    val internalID: String,
    val lastModification: String,
    val owner: Owner,
    val status: String
)