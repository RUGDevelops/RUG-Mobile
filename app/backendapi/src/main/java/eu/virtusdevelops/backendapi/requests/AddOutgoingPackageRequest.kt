package eu.virtusdevelops.backendapi.requests

import java.util.UUID

data class AddOutgoingPackageRequest(
    val recipientEmail: String,
    val recipientFirstName: String,
    val recipientLastName: String,
    val packetHolderId: UUID,
    val street: String,
    val houseNumber: String,
    val city: String,
    val postNumber: String,
    val country: String,
    val tokenFormat: Int
)