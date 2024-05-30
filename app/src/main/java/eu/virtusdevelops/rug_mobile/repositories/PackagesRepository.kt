package eu.virtusdevelops.rug_mobile.repositories

import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import java.util.UUID
import javax.inject.Inject

class PackagesRepository @Inject constructor(
    private val api: ApiRequest
) {



    suspend fun getPackageDetails(packageID: UUID): DeliveryPackage?{
        val response = api.getPackageDetails(packageID)
        if(response.isSuccessful){
            return response.body()
        }

        return null
    }

    suspend fun getIncomingPackages(): List<DeliveryPackage>{
        val response = api.getAllIncomingPackages()
        if(response.isSuccessful){
            return response.body()!!
        }else{
            // throw errors?
        }
        return emptyList()
    }


    suspend fun getOutgoingPackages(): List<DeliveryPackage>{
        val response = api.getAllOutgoingPackages()
        if(response.isSuccessful){
            return response.body()!!
        }else{
            // throw errors?
        }
        return emptyList()
    }

}