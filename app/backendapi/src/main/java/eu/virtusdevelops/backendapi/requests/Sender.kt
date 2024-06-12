package eu.virtusdevelops.backendapi.requests

data class Sender(
    val email: String,
    val firstname: String,
    val lastname: String,
    val verified: Boolean
)