package eu.virtusdevelops.rug_mobile.repositories

import eu.virtusdevelops.backendapi.ApiRequest
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

}