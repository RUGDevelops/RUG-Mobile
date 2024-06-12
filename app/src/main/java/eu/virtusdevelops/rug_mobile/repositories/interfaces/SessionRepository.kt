package eu.virtusdevelops.rug_mobile.repositories.interfaces

import eu.virtusdevelops.datalib.models.SessionInformation
import eu.virtusdevelops.rug_mobile.domain.DataError
import eu.virtusdevelops.rug_mobile.domain.Result
import java.util.UUID

interface SessionRepository {

    suspend fun getActiveSessions(): Result<List<SessionInformation>, DataError.Network>
    suspend fun getPendingSessions(): Result<List<SessionInformation>, DataError.Network>
    suspend fun logoutSession(sessionId: UUID): Result<Boolean, DataError.Network>
    suspend fun approveSession(sessionId: UUID): Result<Boolean, DataError.Network>
    suspend fun declineSession(sessionId: UUID): Result<Boolean, DataError.Network>
}