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



interface ApiRequest {
    @Headers("Content-Type: application/json")
    @GET("package_holder/{id}")
    suspend fun getPackageHolder(@Path("id") id: Int): Response<PackageHolder>

    @Headers("Content-Type: application/json")
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/logout")
    suspend fun logout(): Response<LoginResponse>


    @Headers("Content-Type: application/json")
    @GET("package_holder")
    suspend fun getPackageHolders(): Response<List<PackageHolder>>

}
