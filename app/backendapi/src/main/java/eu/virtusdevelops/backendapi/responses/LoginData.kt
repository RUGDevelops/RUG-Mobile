package eu.virtusdevelops.backendapi.responses

import eu.virtusdevelops.datalib.models.User

data class LoginData(
    val user: User,
    val cookie: String
)
