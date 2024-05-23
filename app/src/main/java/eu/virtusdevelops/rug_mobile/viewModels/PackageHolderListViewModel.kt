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
import eu.virtusdevelops.backendapi.Api
import eu.virtusdevelops.datalib.models.PackageHolder
import kotlinx.coroutines.launch

class PackageHolderListViewModel(
    private val application: Application
)  : ViewModel()  {
    var isBusy by mutableStateOf(false)
    var isError by mutableStateOf(false)
    private val _packageHolders = MutableLiveData<List<PackageHolder>>()
    val packageHolders: LiveData<List<PackageHolder>> get() = _packageHolders



    fun load(){
        viewModelScope.launch {
            isBusy = true
            isError = false
            try{
                val response = Api.api.getPackageHolders()

                println("Status: fetching")

                if(response.isSuccessful){

                    println("Status: ok")
                    val responseBody = response.body()
                    if(responseBody != null){
                        _packageHolders.value = responseBody
                    }
                    println("Status: parsed")


                }else{
                    println("Status: empty ${response.code()}")
                    isError = true
                }
            }catch (ex :Exception){
                isError = true
                ex.printStackTrace()
            }finally {
                isBusy = false
            }
        }
    }
}


val LocalPackageHolderListState = compositionLocalOf<PackageHolderListViewModel> { error("PackageHolderList not found") }