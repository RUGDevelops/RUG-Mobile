package eu.virtusdevelops.rug_mobile.viewModels

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.virtusdevelops.backendapi.Api
import eu.virtusdevelops.backendapi.requests.LoginRequest
import eu.virtusdevelops.rug_mobile.PreferencesManager
import kotlinx.coroutines.launch

class UserViewModel  : ViewModel() {
    var isLoggedIn by mutableStateOf(false)
    var isBusy by mutableStateOf(false)


    fun login(email: String, password: String) {


        // todo save user data somewhere in shared preferences or something


        if(password.isEmpty()){
            isLoggedIn = true
            return
        }

        viewModelScope.launch {
            isBusy = true

            try {

                val response = Api.api.login(
                    LoginRequest(
                        email,
                        password
                    )
                )

                if (response.isSuccessful) {
                    isLoggedIn = true
                } else {
                    isLoggedIn = false
                }

            }catch (ex : Exception){

                ex.printStackTrace()

            }finally {
                isBusy = false
            }

        }


    }

    fun logout() {

        viewModelScope.launch {
            isBusy = true
            try{
                val response = Api.api.logout()

                if(response.isSuccessful){
                    isLoggedIn = false
                    // TODO: remove from shared preferences
                }
            } catch (ex :Exception){

            }finally {
                isBusy = false
            }
        }
    }

}

val LocalUserState = compositionLocalOf<UserViewModel> { error("User State Context Not Found!") }
