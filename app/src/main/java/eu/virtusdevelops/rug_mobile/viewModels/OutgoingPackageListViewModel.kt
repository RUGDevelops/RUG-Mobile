package eu.virtusdevelops.rug_mobile.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.virtusdevelops.backendapi.requests.AddOutgoingPackageRequest
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageRepository
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class OutgoingPackageListViewModel @Inject constructor(
    private val repository: PackageRepository
) : ViewModel() {

    var isBusy by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var isLoaded by mutableStateOf(false)

    private val _deliveryPackages = MutableLiveData<List<DeliveryPackage>>()
    val deliveryPackages: LiveData<List<DeliveryPackage>> get() = _deliveryPackages


    fun load() {
        viewModelScope.launch {
            isBusy = true
            isError = false
            isLoaded = false

            when (val result = repository.getOutgoingPackages()) {
                is Result.Error -> {
                    isError = true
                }

                is Result.Success -> {
                    _deliveryPackages.value = result.data
                }
            }

            isBusy = false
            isLoaded = true

        }
    }

    fun addOutgoingPackage(
        email: String,
        firstName: String,
        lastName: String,
        packageHolder: String,
        streetAddress: String,
        houseNumber: String,
        postNumber: String,
        city: String,
        country: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {
            isBusy = true
            isError = false

            when (val result = repository.addOutgoingPackage(
                AddOutgoingPackageRequest(
                    email,
                    firstName,
                    lastName,
                    UUID.fromString(packageHolder),
                    streetAddress,
                    houseNumber,
                    city,
                    postNumber,
                    country,
                    tokenFormat = 2,
                )
            )) {
                is Result.Error -> {
                    isError = true
                    Log.d("ERROR", result.error.toString())
                }

                is Result.Success -> {
                    onSuccess()
                }
            }

            isBusy = false
            isLoaded = true
        }
    }

}