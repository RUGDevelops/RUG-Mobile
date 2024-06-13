package eu.virtusdevelops.rug_mobile.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageRepository
import eu.virtusdevelops.rug_mobile.repositories.interfaces.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PackageScanViewModel @Inject constructor(
    private val repository: PackageRepository
): ViewModel(){
    var isBusy by mutableStateOf(false)

    private val _errorMessage = MutableStateFlow("")

    var errorMessage = _errorMessage.asStateFlow()

    val isError = _errorMessage
        .map {
            it.isNotEmpty()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(6000), false)




    fun claimPackage(deviceID: Int, onSuccess: (sound: String) -> Unit){

        isBusy = true

        viewModelScope.launch(Dispatchers.IO) {

            val result = repository.claimPackageWithPackageHolderId(deviceID)

            when(result){
                is Result.Success -> {
                    viewModelScope.launch {
                        onSuccess(result.data)
                    }
                }
                is Result.Error -> {
                    // blabla
                    _errorMessage.value = "No package"
                }
            }

            isBusy = false

        }
    }

    fun clearErrorMessage(){
        _errorMessage.value = ""
    }
}