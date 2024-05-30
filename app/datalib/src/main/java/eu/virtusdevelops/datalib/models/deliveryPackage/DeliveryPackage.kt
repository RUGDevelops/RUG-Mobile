package eu.virtusdevelops.datalib.models.deliveryPackage

import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.datalib.models.User
import java.util.Date
import java.util.UUID

data class DeliveryPackage(
    val id: UUID,
    val recipientFirstName: String,
    val recipientLastName: String,
    val recipientEmail: String,
    val sentDate: Date,
    val recievedDate: Date?,
    val estimatedDeliveryDate: Date?,
    val claimToken: UUID,
    val claimed: Boolean,
    val delivered: Boolean,
    val weight: Int,
    val street: String,
    val houseNumber: String,
    val city: String,
    val postNumber: String,
    val country: String,
    val courier: User?,
    val sender: User,
    val pickupPackageHolder: PackageHolder,
    val deliveryPackageHolder: PackageHolder?,
    val statusUpdateList: List<DeliveryPackageStatusUpdate>
)