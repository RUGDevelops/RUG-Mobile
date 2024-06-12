package eu.virtusdevelops.rug_mobile.viewModels

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.virtusdevelops.backendapi.requests.AddOutgoingPackageRequest
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageHolderRepository
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageRepository
import eu.virtusdevelops.rug_mobile.repositories.interfaces.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SendPackageViewModel @Inject constructor(
    private val repository: PackageHolderRepository,
    private val packagesRepository: PackageRepository
): ViewModel() {
    var isBusy by mutableStateOf(false)
        private set
    var isLoaded by mutableStateOf(false)
        private set

    var packageId by mutableStateOf<UUID?>(null)
        private set

    private val _errorMessage = MutableStateFlow("")

    var errorMessage = _errorMessage.asStateFlow()

    val isError = _errorMessage
        .map {
            it.isNotEmpty()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(6000), false)



    private val _internalPackageHolderId = MutableStateFlow<UUID?>(null);
    val internalPackageHolderId = _internalPackageHolderId.asStateFlow()


    fun loadPackageHolder(id: Int?){
        isBusy = true

        if(id == null){
            isBusy = false
            _errorMessage.value = ""
            return
        }

        viewModelScope.launch {
            val result = repository.getPackageHolder(id)

            when(result){
                is Result.Error -> {
                    Log.e("PACKAGEHOLDER", "Failed getting package holder: ${result.error.name}")
                    _errorMessage.value = "Invalid package holder"
                    _internalPackageHolderId.value = null
                }
                is Result.Success -> {
                    Log.i("PACKAGEHOLDER", "Got package holder: ${result.data.internalID}")
                    _internalPackageHolderId.value = result.data.internalID
                }
            }
            isBusy = false


        }

    }

    suspend fun getPackageHolder(id: Int){
        val result = repository.getPackageHolder(id)

        when(result){
            is Result.Error -> {
                Log.e("PACKAGEHOLDER", "Failed getting package holder: ${result.error.name}")
                _errorMessage.value = "Invalid package holder"
                _internalPackageHolderId.value = null
            }
            is Result.Success -> {
                Log.i("PACKAGEHOLDER", "Got package holder: ${result.data.internalID}")
                _internalPackageHolderId.value = result.data.internalID
            }
        }
    }

    fun clearErrorMessage(){
        _errorMessage.value = ""
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
            _errorMessage.value = ""

            getPackageHolder(packageHolder.toInt())

            if(_internalPackageHolderId.value == null){
                _errorMessage.value = "Invalid package holder";
                return@launch
            }


            when (val result = packagesRepository.addOutgoingPackage(
                AddOutgoingPackageRequest(
                    email,
                    firstName,
                    lastName,
                    _internalPackageHolderId.value!!,
                    streetAddress,
                    houseNumber,
                    city,
                    postNumber,
                    country,
                    tokenFormat = 2,
                )
            )) {
                is Result.Error -> {
                    _errorMessage.value = "Something went wrong"
                    Log.d("ERROR", result.error.name)
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