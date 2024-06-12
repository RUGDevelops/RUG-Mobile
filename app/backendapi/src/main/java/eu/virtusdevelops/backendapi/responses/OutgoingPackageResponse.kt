package eu.virtusdevelops.backendapi.responses

import eu.virtusdevelops.backendapi.requests.PackageData

data class OutgoingPackageResponse(
    val openToken: String,
    val packageData: PackageData
)