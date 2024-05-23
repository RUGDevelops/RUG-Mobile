package eu.virtusdevelops.datalib.models

data class User(
    val email: String,
    val firstName: String,
    val lastName: String,
    val verified: Boolean
) {
}