package eu.virtusdevelops.rug_mobile.repositories

import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import java.util.UUID
import javax.inject.Inject

class PackagesRepository @Inject constructor(
    private val api: ApiRequest
) {



    suspend fun getPackageDetails(packageID: UUID): DeliveryPackage?{



        return null
    }

    suspend fun getPackages(): List<DeliveryPackage>{
        val response = api.getAllPackages()


        if(response.isSuccessful){
            return response.body()!!
        }else{
            // throw errors?
        }

        return emptyList()
    }

}