package eu.virtusdevelops.rug_mobile.repositories

import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.backendapi.requests.LoginRequest
import eu.virtusdevelops.backendapi.requests.RegisterRequest
import eu.virtusdevelops.backendapi.responses.LoginData
import eu.virtusdevelops.datalib.models.User
import eu.virtusdevelops.rug_mobile.domain.DataError
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.AuthRepository
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiRequest
)  : AuthRepository {

    override suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        repeatPassword: String,
        deviceToken: String
    ): Result<User, DataError.Network> {

        try{
            val response = api.register(
                RegisterRequest(
                    email,
                    firstName,
                    lastName,
                    password,
                    repeatPassword,
                    deviceToken
                )
            )

            if(response.body() != null){
                val user = response.body()!!
                return Result.Success(user)
            }

        }catch (e : HttpException){
            return when(e.code()) {
                400 -> Result.Error(DataError.Network.BAD_REQUEST)
                408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        }

        return Result.Error(DataError.Network.UNKNOWN)
    }

    override suspend fun login(
        email: String,
        password: String,
        deviceToken: String
    ): Result<LoginData, DataError.Network> {
        try{
            val response = api.login(LoginRequest(
                email,
                password,
                deviceToken
            ))

            if(response.body() != null){
                val loginResponse = response.body()!!

                val cookies = response.headers().get("Set-Cookie")
                var cookie = ""
                if (cookies?.contains("auth_sid") == true) {
                    cookie = cookies.split("=")[1].split(";")[0]
                }


                return Result.Success(LoginData(
                    loginResponse.user,
                    cookie
                ))
            }

        }catch (e : HttpException){
            return when(e.code()) {
                400 -> Result.Error(DataError.Network.BAD_REQUEST)
                408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        }
        return Result.Error(DataError.Network.UNKNOWN)
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        repeatPassword: String
    ): Result<Boolean, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun imageLogin(
        email: String,
        image: ByteArray
    ): Result<LoginData, DataError.Network> {
        val emailPart = RequestBody.create(MediaType.parse("multipart/form-data"), email)
        val deviceTokenPart = RequestBody.create(MediaType.parse("multipart/form-data"), "RANDOM")
        val imagePart = MultipartBody.Part.createFormData(
            "faceImage",
            "faceImage.jpg",
            RequestBody.create(
                MediaType.parse("image/*"),
                image
            )
        )

        try{
            val response = api.faceLogin(imagePart, emailPart, deviceTokenPart)

            if(response.body() != null){
                val loginResponse = response.body()!!

                val cookies = response.headers().get("Set-Cookie")
                var cookie = ""
                if (cookies?.contains("auth_sid") == true) {
                    cookie = cookies.split("=")[1].split(";")[0]
                }


                return Result.Success(LoginData(
                    loginResponse.user,
                    cookie
                ))
            }

        }catch (e : HttpException){
            return when(e.code()) {
                400 -> Result.Error(DataError.Network.BAD_REQUEST)
                408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        }


        return Result.Error(DataError.Network.UNKNOWN)
    }


    override suspend fun logout(): Result<Boolean, DataError.Network> {
        try{
            val response = api.logout()

            if(response.isSuccessful)
                return Result.Success(true)


        }catch (e : HttpException){
            return when(e.code()) {
                400 -> Result.Error(DataError.Network.BAD_REQUEST)
                408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        }
        return Result.Error(DataError.Network.UNKNOWN)
    }

    override suspend fun validateLoginState(): Result<Boolean, DataError.Network> {
        try{
            val response = api.getPackageHolders()

            if(response.body() != null){
                val isSuccessful = response.body()!!
                return Result.Success(isSuccessful.isNotEmpty())
            }

        }catch (e : HttpException){
            return when(e.code()) {
                400 -> Result.Error(DataError.Network.BAD_REQUEST)
                408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        }
        return Result.Error(DataError.Network.UNKNOWN)
    }
}