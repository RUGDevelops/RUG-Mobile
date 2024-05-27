package eu.virtusdevelops.rug_mobile.viewModels

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
import eu.virtusdevelops.backendapi.Api
import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.rug_mobile.repositories.PackageHolderRepository
import kotlinx.coroutines.launch
import java.util.UUID


@HiltViewModel(assistedFactory = PackageHolderViewModel.PackageHolderViewModelFactory::class)
class PackageHolderViewModel  @AssistedInject constructor(
    private val repository: PackageHolderRepository,
    @Assisted
    private val packageHolderID: Int
) : ViewModel() {
    var isBusy by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var isLoaded by mutableStateOf(false)

    private val _packageHolder = MutableLiveData<PackageHolder>()
    val packageHolder: LiveData<PackageHolder> get() = _packageHolder


    fun load(){

        viewModelScope.launch {

            isBusy = true
            isLoaded = false

            try{

                val data = repository.getPackageHolderWithHistory(packageHolderID)
                println("Data: $data")
                if(data == null){
                    isError = true
                }else{
                    _packageHolder.value = data!!
                }





            }catch (ex: Exception){

                isError = true

            }finally {
                isBusy = false
                isLoaded = true
            }
        }


    }






    @AssistedFactory
    interface PackageHolderViewModelFactory {
        fun create(packageHolderID: Int): PackageHolderViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: PackageHolderViewModelFactory,
            packageHolderID: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return assistedFactory.create(packageHolderID) as T
            }
        }
    }

}