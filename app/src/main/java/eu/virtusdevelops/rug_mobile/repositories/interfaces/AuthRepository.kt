package eu.virtusdevelops.rug_mobile.repositories.interfaces

import eu.virtusdevelops.backendapi.responses.LoginData
import eu.virtusdevelops.datalib.models.User
import eu.virtusdevelops.rug_mobile.domain.DataError
import eu.virtusdevelops.rug_mobile.domain.Result

interface AuthRepository {
    suspend fun register(email: String, firstName: String, lastName: String,
                         password: String, repeatPassword: String, deviceToken: String)
    : Result<User, DataError.Network>

    suspend fun login(email: String, password: String, deviceToken: String)
    : Result<LoginData, DataError.Network>

    suspend fun changePassword(oldPassword: String, newPassword: String, repeatPassword: String)
    : Result<Boolean, DataError.Network>

    suspend fun imageLogin(email: String, image: ByteArray)
    : Result<LoginData, DataError.Network>

    suspend fun logout()
    : Result<Boolean, DataError.Network>

    suspend fun validateLoginState()
    : Result<Boolean, DataError.Network>
}