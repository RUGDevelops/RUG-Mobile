package eu.virtusdevelops.rug_mobile.repositories

import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.backendapi.requests.OpenPackageHolderRequest
import eu.virtusdevelops.datalib.models.PackageHolder
import java.util.UUID
import javax.inject.Inject

class  PackageHolderRepository @Inject constructor(
    private val api: ApiRequest
) {




    // throws exceptions
    suspend fun getPackageHolder(packageHolderID: Int): PackageHolder?{
        // call api
        val response = api.getPackageHolderData(packageHolderID)

        if(response.isSuccessful){
            return response.body()
        }else{
            // throw errors?


            return null
        }
    }


    suspend fun getPackageHolderWithHistory(packageHolderID: Int): PackageHolder?{
        // call api
        val response = api.getPackageHolderWithHistory(packageHolderID)

        if(response.isSuccessful){
            val body = response.body()
            if(body != null){
                val packageHolder = body.packageHolder
                packageHolder.history = body.history
                return packageHolder
            }
        }else{
            // throw errors?



        }
        return null
    }


    suspend fun getPackageHolders(): List<PackageHolder>?{
        val response = api.getPackageHolders()

        if(response.isSuccessful){
            if(response.body() != null){
                return response.body()
            }
        }else{
            // throw errors?


            return null
        }

        return null
    }


    suspend fun getPackageHolderOpenSound(packageHolderID: Int): String{
        val response = api.getPackageHolderOpenSound(packageHolderID, OpenPackageHolderRequest(2))
        if(response.isSuccessful){
            val responseBody = response.body()
            if(responseBody != null){
                return responseBody.data
            }else{
                return ""
            }
        }
        return ""
    }

}