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
import eu.virtusdevelops.rug_mobile.repositories.PackagesRepository
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel(assistedFactory = PackageViewModel.PackageViewModelFactory::class)
class PackageViewModel @AssistedInject constructor(
    private val application: Application,
    private val repository: PackagesRepository,
    @Assisted
    private val packageID: UUID
): ViewModel(){

    var isBusy by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var isLoaded by mutableStateOf(false)

    private val _deliveryPackage = MutableLiveData<DeliveryPackage?>()
    val deliveryPackage: LiveData<DeliveryPackage?> get() = _deliveryPackage




    fun load(){
        viewModelScope.launch {
            isBusy = true
            isError = false
            isLoaded = false
            try{
                val packageDetails = repository.getPackageDetails(packageID)
                _deliveryPackage.value = packageDetails

            }catch (ex :Exception){
                isError = true
                ex.printStackTrace()
            }finally {
                isBusy = false
                isLoaded = true
            }
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