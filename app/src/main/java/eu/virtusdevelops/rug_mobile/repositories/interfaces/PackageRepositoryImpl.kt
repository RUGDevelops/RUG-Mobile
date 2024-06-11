package eu.virtusdevelops.rug_mobile.repositories.interfaces

import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.rug_mobile.domain.DataError
import eu.virtusdevelops.rug_mobile.domain.Result
import java.util.UUID
import javax.inject.Inject

class PackageRepositoryImpl @Inject constructor(
    private val api: ApiRequest
) : PackageRepository {
    override suspend fun getPackageDetails(packageID: UUID): Result<DeliveryPackage, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun getIncomingPackages(): Result<List<DeliveryPackage>, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun getOutgoingPackages(): Result<List<DeliveryPackage>, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun getOpenSound(packageID: UUID): Result<String, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun verifySendingPackage(packageID: UUID): Result<DeliveryPackage, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun claimPackage(packageID: UUID): Result<String, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun deliveryPickup(packageID: UUID): Result<String, DataError.Network> {
        TODO("Not yet implemented")
    }

    override suspend fun setOnRoute(packageID: UUID): Result<DeliveryPackage, DataError.Network> {
        TODO("Not yet implemented")
    }
}