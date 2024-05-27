package eu.virtusdevelops.datalib.models

import java.util.Date
import java.util.UUID

data class PackageHolder(
    val id: Int,
    val internalID: UUID,
    val status: PackageHolderStatus,
    val owner: User,
    var history: List<PackageHolderAction>,
    val lastModification: Date
)
