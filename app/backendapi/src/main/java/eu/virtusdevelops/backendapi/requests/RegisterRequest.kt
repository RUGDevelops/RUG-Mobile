package eu.virtusdevelops.backendapi.requests

data class RegisterRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val repeatPassword: String
)
