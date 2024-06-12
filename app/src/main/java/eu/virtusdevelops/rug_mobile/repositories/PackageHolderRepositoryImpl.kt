package eu.virtusdevelops.rug_mobile.repositories

import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.backendapi.requests.AddPackageHolderRequest
import eu.virtusdevelops.backendapi.requests.OpenPackageHolderRequest
import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.datalib.models.PackageHolderAction
import eu.virtusdevelops.rug_mobile.domain.DataError
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageHolderRepository
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class PackageHolderRepositoryImpl @Inject constructor(
    private val api: ApiRequest
): PackageHolderRepository {

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    override suspend fun getPackageHolder(packageHolderID: Int): Result<PackageHolder, DataError.Network> {

        try{
            val response = api.getPackageHolder(packageHolderID);

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

    override suspend fun getPackageHolderWithHistory(packageHolderID: Int): Result<PackageHolder, DataError.Network> {
        try{
            val response = api.getPackageHolderWithHistory(packageHolderID);

            if(response.body() != null){
                val body = response.body()!!
                val packageHolder = body.packageHolder

                return Result.Success(
                    PackageHolder(
                        packageHolder.id,
                        packageHolder.internalID,
                        packageHolder.status,
                        packageHolder.owner,
                        splitHistoryByDays(body.history),
                        packageHolder.lastModification
                    )
                )
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

    override suspend fun getPackageHolders(): Result<List<PackageHolder>, DataError.Network> {
        try{
            val response = api.getPackageHolders();

            if(response.body() != null){
                val body = response.body()!!

                return Result.Success(
                    body
                )
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

    override suspend fun getPackageHolderOpenSound(packageHolderID: Int): Result<String, DataError.Network> {
        try{
            val response = api.getPackageHolderOpenSound(packageHolderID, OpenPackageHolderRequest(2));

            if(response.body() != null){
                val body = response.body()!!

                return Result.Success(
                    body.data
                )
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

    override suspend fun deletePackageHolder(packageHolderID: Int): Result<Boolean, DataError.Network> {
        try{
            val response = api.deletePackageHolder(packageHolderID);

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

    override suspend fun addPackageHolder(packageHolderID: Int, isPublic: Boolean): Result<PackageHolder, DataError.Network> {
        try{
            val response = api.addPackageHolder(AddPackageHolderRequest(
                packageHolderID,
                isPublic
            ));


            if(response.body() != null){
                val body = response.body()!!
                return Result.Success(body);
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



    private fun splitHistoryByDays(history: List<PackageHolderAction>): Map<String, List<PackageHolderAction>>{
        val sortedHistory = history.sortedByDescending { it.date }
        return sortedHistory.groupBy { dateFormat.format(it.date) }

    }
}