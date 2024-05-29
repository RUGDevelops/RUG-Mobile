package eu.virtusdevelops.datalib.models

import java.util.Date

data class PackageHolderAction(
    val status: PackageHolderActionStatus,
    val date: Date
)
