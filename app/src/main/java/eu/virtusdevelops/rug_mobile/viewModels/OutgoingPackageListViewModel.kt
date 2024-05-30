package eu.virtusdevelops.rug_mobile.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.rug_mobile.repositories.PackagesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OutgoingPackageListViewModel @Inject constructor(
    private val application: Application,
    private val repository: PackagesRepository
): ViewModel(){

    var isBusy by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var isLoaded by mutableStateOf(false)

    private val _deliveryPackages = MutableLiveData<List<DeliveryPackage>>()
    val deliveryPackages: LiveData<List<DeliveryPackage>> get() = _deliveryPackages




    fun load(){
        viewModelScope.launch {
            isBusy = true
            isError = false
            isLoaded = false
            try{
                val holders = repository.getOutgoingPackages()
                _deliveryPackages.value = holders
            }catch (ex :Exception){
                isError = true
                ex.printStackTrace()
            }finally {
                isBusy = false
                isLoaded = true
            }
        }
    }

}