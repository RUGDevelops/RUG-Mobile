package eu.virtusdevelops.datalib.models

import java.util.UUID

data class SessionInformation(
    val id: UUID,
    val ip: String,
    val location: String,
    val userAgent: String,
    val status: SessionInformationStatus
)
