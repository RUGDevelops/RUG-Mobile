package eu.virtusdevelops.rug_mobile.viewModels

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.virtusdevelops.backendapi.Api
import eu.virtusdevelops.datalib.models.User
import eu.virtusdevelops.rug_mobile.dataStore
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val application: Application,
    private val authRepository: AuthRepository,
    private val apiObject: Api
) : ViewModel() {

    var isLoggedIn by mutableStateOf(false)
    var isBusy by mutableStateOf(true)
    var user: User? by mutableStateOf(null)
    var error: String? by mutableStateOf(null)

    private val LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val EMAIL_KEY = stringPreferencesKey("user_email")
    private val NAME_KEY = stringPreferencesKey("user_first_name")
    private val LASTNAME_KEY = stringPreferencesKey("user_last_name")
    private val COOKIE_PATH = stringPreferencesKey("user_cookie")
    private val VERIFIED_PATH = booleanPreferencesKey("is_verified")

    init {
        isBusy = true
        viewModelScope.launch {
            //saveUserPreferences(false, "", "", "", "", false)
            loadUser()

            // validate login state on backend?
            val result = authRepository.validateLoginState()
            when(result){
                is Result.Error -> {
                    isLoggedIn = false
                    val tempUser = user ?: User("", "", "", false)
                    saveUserPreferences(false, tempUser.email, tempUser.firstname, tempUser.lastname, "", false)
                }
                is Result.Success -> {

                }
            }
            isBusy = false
        }
    }

    fun register(email: String, password: String, firstName: String, lastName: String, repeatPassword: String, onSuccess: () -> Unit) {
        if (password.isEmpty() && repeatPassword.isEmpty()) {
            isLoggedIn = true
            onSuccess()
            return
        }

        viewModelScope.launch {
            isBusy = true
            error = null
            val result = authRepository.register(
                email,
                firstName,
                lastName,
                password,
                repeatPassword,
                "TODO_DEVICE_TOKEN"
            );
            when(result){
                is Result.Error -> {
                    isLoggedIn = false
                    error = result.error.name
                }
                is Result.Success -> {
                    onSuccess()
                }
            }
            isBusy = false
        }
    }

    fun changePassword(oldPassword: String, newPassword: String, repeatPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isBusy = true
            error = null

            val result = authRepository.changePassword(
                oldPassword,
                newPassword,
                repeatPassword
            )

            when(result){
                is Result.Error -> {
                    error = result.error.name
                }
                is Result.Success -> {
                    onSuccess()
                }
            }
            isBusy = false
        }
    }

    fun loginWithImage(email: String, image: Bitmap){
        isBusy = true
        viewModelScope.launch(Dispatchers.IO) {
            isLoggedIn = false

            val result = authRepository.imageLogin(
                email,
                imageToBitmap(image)
            )

            when(result){
                is Result.Error -> {
                    isLoggedIn = false
                    error = result.error.name

                    if(user != null)
                        saveUserPreferences(false, user!!.email, user!!.firstname, user!!.lastname, "", false)
                    else
                        saveUserPreferences(false, "", "", "", "", false)

                    apiObject.setCookie("")
                    Log.e("LOGIN", "Failed: ${result.error.name}")

                }
                is Result.Success -> {
                    val loginResponse = result.data
                    val tempUser = loginResponse.user
                    val cookie = loginResponse.cookie

                    saveUserPreferences(
                        true,
                        tempUser.email,
                        tempUser.firstname,
                        tempUser.lastname,
                        cookie,
                        tempUser.verified
                    )
                    user = tempUser
                    apiObject.setCookie(cookie)
                    isLoggedIn = true
                }
            }
            isBusy = false
        }
    }

    private fun imageToBitmap(image: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
    }

    fun login(email: String, password: String) {

        if (password.isEmpty()) {
            isLoggedIn = true
            return
        }

        viewModelScope.launch {
            isBusy = true
            error = null


            val result = authRepository.login(
                email,
                password,
                "TODO_DEVICE_TOKEN"
            )

            when(result){
                is Result.Error -> {
                    isLoggedIn = false
                    error = result.error.name

                    if(user != null)
                        saveUserPreferences(false, user!!.email, user!!.firstname, user!!.lastname, "", false)
                    else
                        saveUserPreferences(false, "", "", "", "", false)

                    apiObject.setCookie("")
                    Log.e("LOGIN", "Failed: ${result.error.name}")

                }
                is Result.Success -> {
                    isLoggedIn = true
                    val loginResponse = result.data
                    val tempUser = loginResponse.user
                    val cookie = loginResponse.cookie

                    saveUserPreferences(
                        true,
                        tempUser.email,
                        tempUser.firstname,
                        tempUser.lastname,
                        cookie,
                        tempUser.verified
                    )

                    user = tempUser
                    apiObject.setCookie(cookie)
                }
            }
            isBusy = false
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isBusy = true

            val result = authRepository.logout()

            when(result){
                is Result.Success -> {
                    isLoggedIn = false
                    user = null
                    saveUserPreferences(false, "", "", "", "", false)
                    apiObject.setCookie("")
                    onSuccess()
                }
                is Result.Error -> {
                    Log.e("AUTH", result.error.name)
                }
            }
            isBusy = false;
        }
    }

    private suspend fun loadUser() {
        try {
            val preferences = application.dataStore.data
                .map { preferences ->
                    UserPreferences(
                        preferences[LOGGED_IN] ?: false,
                        preferences[EMAIL_KEY] ?: "",
                        preferences[NAME_KEY] ?: "",
                        preferences[LASTNAME_KEY] ?: "",
                        preferences[COOKIE_PATH] ?: "",
                        preferences[VERIFIED_PATH] ?: false
                    )
                }.firstOrNull()
            if (preferences != null) {
                user = User(
                    preferences.email,
                    preferences.firstName,
                    preferences.lastName,
                    preferences.verified
                )
                isLoggedIn = preferences.isLoggedIn
                apiObject.setCookie(preferences.cookie)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    private suspend fun saveUserPreferences(
        loggedIn: Boolean,
        email: String,
        firstName: String,
        lastName: String,
        cookie: String,
        verified: Boolean
    ) {
        application.dataStore.edit { preferences ->
            preferences[LOGGED_IN] = loggedIn
            preferences[EMAIL_KEY] = email
            preferences[NAME_KEY] = firstName
            preferences[LASTNAME_KEY] = lastName
            preferences[COOKIE_PATH] = cookie
            preferences[VERIFIED_PATH] = verified
        }
    }

    private data class UserPreferences(
        val isLoggedIn: Boolean,
        val email: String,
        val firstName: String,
        val lastName: String,
        val cookie: String,
        val verified: Boolean
    )

}

//val LocalUserState = compositionLocalOf<UserViewModel> { error("User State Context Not Found!") }
