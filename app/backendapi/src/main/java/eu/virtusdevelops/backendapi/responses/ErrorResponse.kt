package eu.virtusdevelops.backendapi.responses

data class LoginResponse(
    val email: String,
    val firstname: String,
    val lastname: String,
    val errors: List<String>
)
