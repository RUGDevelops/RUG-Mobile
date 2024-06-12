package eu.virtusdevelops.rug_mobile.repositories.interfaces

import eu.virtusdevelops.backendapi.requests.AddOutgoingPackageRequest
import eu.virtusdevelops.backendapi.requests.PackageData
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.rug_mobile.domain.DataError
import eu.virtusdevelops.rug_mobile.domain.Result
import java.util.UUID

interface PackageRepository {
    suspend fun getPackageDetails(packageID: UUID): Result<DeliveryPackage, DataError.Network>
    suspend fun getIncomingPackages(): Result<List<DeliveryPackage>, DataError.Network>
    suspend fun getOutgoingPackages(): Result<List<DeliveryPackage>, DataError.Network>
    suspend fun addOutgoingPackage(packageRequest: AddOutgoingPackageRequest): Result<PackageData, DataError.Network>
    suspend fun getDepositSound(packageID: UUID): Result<String, DataError.Network>
    suspend fun verifySendingPackage(packageID: UUID): Result<DeliveryPackage, DataError.Network>
    suspend fun claimPackage(packageID: UUID): Result<String, DataError.Network> // returns sound
    suspend fun deliveryPickup(packageID: UUID): Result<String, DataError.Network> // returns sound
    suspend fun setOnRoute(packageID: UUID): Result<DeliveryPackage, DataError.Network>
}