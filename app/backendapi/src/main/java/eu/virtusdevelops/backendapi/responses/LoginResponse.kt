package eu.virtusdevelops.backendapi.responses

import eu.virtusdevelops.datalib.models.User
import java.util.UUID

data class LoginResponse(
    val user: User,
    val status: String,
    val loginToken: UUID
)