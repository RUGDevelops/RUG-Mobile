package eu.virtusdevelops.backendapi.responses

import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.datalib.models.PackageHolderAction

data class PackageHolderWithHistoryResponse(
    val packageHolder: PackageHolder,
    val history: List<PackageHolderAction>
) {
}