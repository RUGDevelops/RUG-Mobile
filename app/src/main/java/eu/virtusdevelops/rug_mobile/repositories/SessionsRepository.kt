package eu.virtusdevelops.rug_mobile.repositories

import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.backendapi.requests.OpenPackageHolderRequest
import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.datalib.models.PackageHolderAction
import eu.virtusdevelops.datalib.models.SessionInformation
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class  SessionsRepository @Inject constructor(
    private val api: ApiRequest
) {

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())



    // throws exceptions
    suspend fun getActiveSessions(): List<SessionInformation>{
        // call api
        val response = api.getActiveSessions()

        if(response.isSuccessful){
            val body = response.body();
            if(body != null){
                return body
            }
            return emptyList()
        }else{
            // throw errors?
            return emptyList()
        }
    }


    suspend fun getPendingSessions(): List<SessionInformation>{
        // call api
        val response = api.getPendingSessions()

        if(response.isSuccessful){
            val body = response.body();
            if(body != null){
                return body
            }
            return emptyList()
        }else{
            // throw errors?
            return emptyList()
        }
    }


    suspend fun logoutSession(sessionId: UUID): Boolean{
        val response = api.logoutSession(sessionId)
        if(response.isSuccessful){
            val body = response.body();
            if(body != null){
                return body
            }
            return false
        }else{
            // throw errors?
            return false
        }
    }


    suspend fun approveSession(sessionId: UUID): Boolean{
        val response = api.acceptSession(sessionId)
        if(response.isSuccessful){
            val body = response.body();
            if(body != null){
                return body
            }
            return false
        }else{
            // throw errors?
            return false
        }
    }


    suspend fun declineSession(sessionId: UUID): Boolean{
        val response = api.declineSession(sessionId)
        if(response.isSuccessful){
            val body = response.body();
            if(body != null){
                return body
            }
            return false
        }else{
            // throw errors?
            return false
        }
    }




}