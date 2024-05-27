package eu.virtusdevelops.datalib.models

data class User(
    val email: String,
    val firstname: String,
    val lastname: String,
    val verified: Boolean
) {
}