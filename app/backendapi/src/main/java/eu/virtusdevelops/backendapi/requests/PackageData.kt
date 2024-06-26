package eu.virtusdevelops.backendapi.requests

data class PackageData(
    val id: String,
    val recipientFirstName: String,
    val recipientLastName: String,
    val recipientEmail: String,
    val sentDate: String,
    val recievedDate: Any,
    val estimatedDeliveryDate: Any,
    val claimToken: String,
    val claimed: Boolean,
    val delivered: Boolean,
    val weight: Int,
    val street: String,
    val houseNumber: String,
    val city: String,
    val postNumber: String,
    val country: String,
    val statusUpdateList: List<StatusUpdate>,
    val courier: Any,
    val sender: Sender,
    val pickupPackageHolder: PickupPackageHolder,
    val deliveryPackageHolder: DeliveryPackageHolder,
)