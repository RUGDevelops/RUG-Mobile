package eu.virtusdevelops.rug_mobile.viewModels

import android.app.Application
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.virtusdevelops.backendapi.Api
import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.rug_mobile.repositories.PackageHolderRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PackageHolderListViewModel @Inject constructor(
    private val application: Application,
//    private val api: ApiRequest,
    private val repository: PackageHolderRepository,
)  : ViewModel()  {
    var isBusy by mutableStateOf(false)
    var isLoadingSound by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var isLoaded by mutableStateOf(false)
    var openSound by mutableStateOf("")
    private val _packageHolders = MutableLiveData<List<PackageHolder>>()
    val packageHolders: LiveData<List<PackageHolder>> get() = _packageHolders


    fun load(){
        viewModelScope.launch {
            isBusy = true
            isError = false
            try{
                val holders = repository.getPackageHolders()

                if(holders != null){
                    _packageHolders.value = holders!!
                }else{
                    isError = true
                }

            }catch (ex :Exception){
                isError = true
                ex.printStackTrace()
            }finally {
                isBusy = false
                isLoaded = true
            }
        }
    }

    fun getOpenSound(id: Int, onSuccess: () -> Unit){
        openSound = ""
//        isBusy = true
        isLoadingSound = true
        viewModelScope.launch {
            try{

                val sound = repository.getPackageHolderOpenSound(id)
                openSound = sound
                onSuccess()
            }finally {
                isLoadingSound = false
//                isBusy = false
            }
        }
    }

}
