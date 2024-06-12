package eu.virtusdevelops.rug_mobile.repositories.interfaces

import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.rug_mobile.domain.DataError
import eu.virtusdevelops.rug_mobile.domain.Result

interface PackageHolderRepository {
    suspend fun getPackageHolder(packageHolderID: Int): Result<PackageHolder, DataError.Network>
    suspend fun getPackageHolderWithHistory(packageHolderID: Int): Result<PackageHolder, DataError.Network>
    suspend  fun getPackageHolders(): Result<List<PackageHolder>, DataError.Network>
    suspend fun getPackageHolderOpenSound(packageHolderID: Int): Result<String, DataError.Network>
    suspend fun deletePackageHolder(packageHolderID: Int): Result<Boolean, DataError.Network>
    suspend fun addPackageHolder(packageHolderID: Int, isPublic: Boolean): Result<PackageHolder, DataError.Network>
}