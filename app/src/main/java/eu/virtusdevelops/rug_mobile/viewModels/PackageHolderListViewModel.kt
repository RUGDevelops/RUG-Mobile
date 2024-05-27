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
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PackageHolderListViewModel @Inject constructor(
    private val application: Application,
    private val api: ApiRequest,
    private val apiObject: Api
)  : ViewModel()  {
    var isBusy by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var isLoaded by mutableStateOf(false)
    private val _packageHolders = MutableLiveData<List<PackageHolder>>()
    val packageHolders: LiveData<List<PackageHolder>> get() = _packageHolders

    init {
        println("Loading view model!!")
    }

    fun load(){
        viewModelScope.launch {

            println("New Cookie: ${apiObject.getCookie()}")

            isBusy = true
            isError = false
            try{
                val response = api.getPackageHolders()

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
                isLoaded = true
            }
        }
    }
}
