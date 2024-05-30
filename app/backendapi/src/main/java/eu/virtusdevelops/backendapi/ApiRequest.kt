package eu.virtusdevelops.backendapi
import eu.virtusdevelops.backendapi.requests.AddPackageHolderRequest
import eu.virtusdevelops.backendapi.requests.LoginRequest
import eu.virtusdevelops.backendapi.requests.OpenPackageHolderRequest
import eu.virtusdevelops.backendapi.requests.RegisterRequest
import eu.virtusdevelops.backendapi.responses.LoginResponse
import eu.virtusdevelops.backendapi.responses.PackageHolderOpenResponse
import eu.virtusdevelops.backendapi.responses.PackageHolderWithHistoryResponse
import eu.virtusdevelops.backendapi.responses.RegisterResponse
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.datalib.models.PackageHolder
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID


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
    suspend fun logout(): Response<Void>

    @Headers("Content-Type: application/json")
    @GET("package_holder")
    suspend fun getPackageHolders(): Response<List<PackageHolder>>


    @Headers("Content-Type: application/json")
    @POST("package_holder/{id}/open")
    suspend fun getPackageHolderOpenSound(@Path("id") id: Int, @Body openRequest: OpenPackageHolderRequest): Response<PackageHolderOpenResponse>


    @Headers("Content-Type: application/json")
    @GET("package_holder/{id}")
    suspend fun getPackageHolderData(@Path("id") id: Int): Response<PackageHolder>

    @Headers("Content-Type: application/json")
    @GET("package_holder/{id}/history")
    suspend fun getPackageHolderWithHistory(@Path("id") id: Int): Response<PackageHolderWithHistoryResponse>


    @Headers("Content-Type: application/json")
    @POST("package_holder/add")
    suspend fun addPackageHolder(@Body packageHolderData: AddPackageHolderRequest): Response<PackageHolder>


    @Headers("Content-Type: application/json")
    @GET("package/{id}")
    suspend fun getPackageDetails(@Path("id") id: UUID): Response<DeliveryPackage>


    @Headers("Content-Type: application/json")
    @GET("package")
    suspend fun getAllIncomingPackages(): Response<List<DeliveryPackage>>

    @Headers("Content-Type: application/json")
    @GET("package/outgoing")
    suspend fun getAllOutgoingPackages(): Response<List<DeliveryPackage>>
}
