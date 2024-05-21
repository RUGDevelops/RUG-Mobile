package eu.virtusdevelops.backendapi
import eu.virtusdevelops.backendapi.requests.LoginRequest
import eu.virtusdevelops.backendapi.requests.RegisterRequest
import eu.virtusdevelops.backendapi.responses.LoginResponse
import eu.virtusdevelops.backendapi.responses.RegisterResponse
import eu.virtusdevelops.datalib.models.PackageHolder
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

//
//const val BASE_URL = "https://api-d4me-stage.direct4.me"
//const val API_KEY = "9ea96945-3a37-4638-a5d4-22e89fbc998f"

interface ApiRequest {
    @Headers("Content-Type: application/json")
    @GET("/package_holder/{id}")
    suspend fun getPackageHolder(@Path("id") id: Int): Response<PackageHolder>

    @Headers("Content-Type: application/json")
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

}
