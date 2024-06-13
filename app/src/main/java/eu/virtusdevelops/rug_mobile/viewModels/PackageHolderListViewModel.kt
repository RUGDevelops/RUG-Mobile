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
import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageHolderRepository
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.IO) {
            isBusy = true
            isError = false

            when(val result = repository.getPackageHolders()){
                is Result.Success -> {

                    viewModelScope.launch {
                        _packageHolders.value = result.data
                    }
                }
                is Result.Error -> {
                    viewModelScope.launch {
                        isError = true
                    }
                }
            }
            isBusy = false
            isLoaded = true
        }
    }

    fun getOpenSound(id: Int, onSuccess: () -> Unit){
        openSound = ""
        viewModelScope.launch(Dispatchers.IO) {
            isLoadingSound = true

            when(val result = repository.getPackageHolderOpenSound(id)){
                is Result.Success -> {
                    viewModelScope.launch {
                        openSound = result.data
                        onSuccess()
                    }
                }
                is Result.Error -> {

                }
            }
            viewModelScope.launch {
                isLoadingSound = false
            }
        }
    }

}
