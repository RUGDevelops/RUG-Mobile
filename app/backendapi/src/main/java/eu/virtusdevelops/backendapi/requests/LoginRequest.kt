package eu.virtusdevelops.backendapi.requests

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceToken: String
)
