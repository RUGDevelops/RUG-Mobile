package eu.virtusdevelops.rug_mobile.repositories

import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.datalib.models.SessionInformation
import eu.virtusdevelops.rug_mobile.domain.DataError
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.SessionRepository
import retrofit2.HttpException
import java.util.UUID
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val api: ApiRequest
) : SessionRepository {
    override suspend fun getActiveSessions(): Result<List<SessionInformation>, DataError.Network> {
        try{
            val response = api.getActiveSessions();

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

    override suspend fun getPendingSessions(): Result<List<SessionInformation>, DataError.Network> {
        try{
            val response = api.getPendingSessions();

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

    override suspend fun logoutSession(sessionId: UUID): Result<Boolean, DataError.Network> {
        try{
            val response = api.logoutSession(sessionId);

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

    override suspend fun approveSession(sessionId: UUID): Result<Boolean, DataError.Network> {
        try{
            val response = api.acceptSession(sessionId);

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

    override suspend fun declineSession(sessionId: UUID): Result<Boolean, DataError.Network> {
        try{
            val response = api.declineSession(sessionId);

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

}