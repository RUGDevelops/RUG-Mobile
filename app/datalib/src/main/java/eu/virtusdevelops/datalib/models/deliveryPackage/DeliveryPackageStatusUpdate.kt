package eu.virtusdevelops.datalib.models.deliveryPackage

import java.util.Date

data class DeliveryPackageStatusUpdate (
    val status: DeliveryPackageStatus,
    val date: Date
)