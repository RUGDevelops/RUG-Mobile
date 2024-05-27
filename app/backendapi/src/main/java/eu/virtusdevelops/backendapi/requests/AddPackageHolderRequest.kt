package eu.virtusdevelops.backendapi.requests

data class AddPackageHolderRequest(
    val deviceID: Int,
    val isPublic: Boolean
)
