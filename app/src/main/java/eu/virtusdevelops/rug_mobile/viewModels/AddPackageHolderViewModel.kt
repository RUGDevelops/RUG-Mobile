package eu.virtusdevelops.rug_mobile.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageHolderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddPackageHolderViewModel @Inject constructor(
    private val repository: PackageHolderRepository
): ViewModel() {
    var isBusy by mutableStateOf(false)
        private set

    var packageId by mutableStateOf<UUID?>(null)
        private set

    private val _errorMessage = MutableStateFlow("")

    val isError = _errorMessage
        .map {
            it.isNotEmpty()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(6000), false)



    fun addPackageHolder(id: Int?, onSuccess: () -> Unit){
        isBusy = true

        if(id == null){
            isBusy = false
            _errorMessage.value = ""
            return
        }

        viewModelScope.launch {
            val result = repository.addPackageHolder(id, false)

            when(result){
                is Result.Error -> {
                    Log.e("PACKAGEHOLDER", "Invalid package holder: ${result.error.name}")
                    _errorMessage.value = result.error.name
                }
                is Result.Success -> {
                    Log.i("PACKAGEHOLDER", "Okay added package holder: ${result.data.internalID}")
                    onSuccess()
                }
            }
            isBusy = false
        }
    }

    fun clearErrorMessage(){
        _errorMessage.value = ""
    }
}