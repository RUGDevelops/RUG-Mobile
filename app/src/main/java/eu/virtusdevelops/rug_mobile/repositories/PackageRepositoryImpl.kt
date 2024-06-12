package eu.virtusdevelops.rug_mobile.repositories

import android.util.Log
import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.backendapi.requests.AddOutgoingPackageRequest
import eu.virtusdevelops.backendapi.requests.OpenPackageHolderRequest
import eu.virtusdevelops.backendapi.requests.PackageData
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.rug_mobile.domain.DataError
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageRepository
import retrofit2.HttpException
import java.util.UUID
import javax.inject.Inject

class PackageRepositoryImpl @Inject constructor(
    private val api: ApiRequest
) : PackageRepository {
    override suspend fun getPackageDetails(packageID: UUID): Result<DeliveryPackage, DataError.Network> {

        try{
            val response = api.getPackageDetails(packageID)

            if(response.body() != null){
                val body = response.body()!!
                return Result.Success(body)
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

    override suspend fun getIncomingPackages(): Result<List<DeliveryPackage>, DataError.Network> {
        try{
            val response = api.getAllIncomingPackages()

            if(response.body() != null){
                val body = response.body()!!
                return Result.Success(body)
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

    override suspend fun getOutgoingPackages(): Result<List<DeliveryPackage>, DataError.Network> {
        try{
            val response = api.getAllOutgoingPackages()

            if(response.body() != null){
                val body = response.body()!!
                return Result.Success(body)
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

    override suspend fun addOutgoingPackage(packageRequest: AddOutgoingPackageRequest): Result<PackageData, DataError.Network> {
        try {
            Log.d("REQUEST", packageRequest.toString())
            val response = api.sendPackage(packageRequest)
            if(response.body() != null){
                 val body = response.body()!!
                 Log.d("RESPONSE", body.toString())
                 return Result.Success(body.packageData)
             }
        } catch(e: HttpException) {
            return when(e.code()) {
                400 -> Result.Error(DataError.Network.BAD_REQUEST)
                408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        }
        return Result.Error(DataError.Network.UNKNOWN)
    }

    override suspend fun getDepositSound(packageID: UUID): Result<String, DataError.Network> {
        try{
            val response = api.openPackageHolderToDeposit(packageID, OpenPackageHolderRequest(2))

            if(response.body() != null){
                val body = response.body()!!
                return Result.Success(body.data)
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

    override suspend fun verifySendingPackage(packageID: UUID): Result<DeliveryPackage, DataError.Network> {
        try{
            val response = api.verifyPackageSend(packageID)

            if(response.body() != null){
                val body = response.body()!!
                return Result.Success(body.packageData)
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

    override suspend fun claimPackage(packageID: UUID): Result<String, DataError.Network> {
        try{
            val response = api.claimPackage(packageID)

            if(response.body() != null){
                val body = response.body()!!
                return Result.Success(body.openToken)
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

    override suspend fun deliveryPickup(packageID: UUID): Result<String, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun setOnRoute(packageID: UUID): Result<DeliveryPackage, DataError.Network> {
        TODO("Not yet implemented")
    }
}