package eu.virtusdevelops.rug_mobile.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageRepository
import kotlinx.coroutines.launch
import java.util.UUID


@HiltViewModel(assistedFactory = PackageViewModel.PackageViewModelFactory::class)
class PackageViewModel @AssistedInject constructor(
    private val application: Application,
    private val repository: PackageRepository,
    @Assisted
    private val packageID: UUID
): ViewModel(){

    var isBusy by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var isLoaded by mutableStateOf(false)


    var openSound by mutableStateOf("")
    var isOpenError by mutableStateOf(false)
    var isVerifyError by mutableStateOf(false)


    private val _deliveryPackage = MutableLiveData<DeliveryPackage?>()
    val deliveryPackage: LiveData<DeliveryPackage?> get() = _deliveryPackage




    fun load(){
        viewModelScope.launch {
            isBusy = true
            isError = false
            isLoaded = false

            when(val result = repository.getPackageDetails(packageID)){
                is Result.Error -> {
                    isError = true
                }
                is Result.Success -> {
                    _deliveryPackage.value = result.data
                }
            }

            isBusy = false
            isLoaded = true

        }
    }


    fun openPackageHolder(onSuccess: () -> Unit){

        viewModelScope.launch {
            isBusy = true
            isOpenError = false

            when(val result = repository.getDepositSound(packageID)){
                is Result.Error -> {
                    isOpenError = true
                }
                is Result.Success -> {
                    openSound = result.data

                    onSuccess()

                }
            }

            isBusy = false

        }
    }



    fun verifySendPackage(){

        viewModelScope.launch {
            isBusy = true
            isVerifyError = false

            when(val result = repository.verifySendingPackage(packageID)){
                is Result.Error -> {
                    isVerifyError = true
                }
                is Result.Success -> {
                    _deliveryPackage.value = result.data
                }
            }

            isBusy = false

        }
    }



    @AssistedFactory
    interface PackageViewModelFactory {
        fun create(packageID: UUID): PackageViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: PackageViewModelFactory,
            packageID: UUID
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return assistedFactory.create(packageID) as T
            }
        }
    }

}