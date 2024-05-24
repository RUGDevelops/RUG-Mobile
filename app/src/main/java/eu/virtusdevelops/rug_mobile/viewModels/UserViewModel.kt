package eu.virtusdevelops.rug_mobile.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.virtusdevelops.backendapi.Api
import eu.virtusdevelops.backendapi.Api.getErrorResponse
import eu.virtusdevelops.backendapi.requests.LoginRequest
import eu.virtusdevelops.backendapi.requests.RegisterRequest
import eu.virtusdevelops.backendapi.responses.ErrorResponse
import eu.virtusdevelops.datalib.models.User
import eu.virtusdevelops.rug_mobile.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UserViewModel(
    private val application: Application
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
            isBusy = false
        }
    }

    fun register(email: String, password: String, firstName: String, lastName: String, repeatPassword: String, onSuccess: () -> Unit) {
        if (password.isEmpty()) {
            isLoggedIn = true
            return
        }

        viewModelScope.launch {
            isBusy = true
            error = null

            try {
                val response = Api.api.register(
                    RegisterRequest(
                        email,
                        password,
                        firstName,
                        lastName,
                        repeatPassword
                    )
                )

                if (response.isSuccessful) {
                    isLoggedIn = false
                    val data = response.body()

                    val cookies = response.headers().get("Set-Cookie")
                    var cookie = ""
                    if (cookies?.contains("auth_sid") == true) {
                        cookie = cookies.split("=")[1].split(";")[0]
                    }

                    if (data != null) {
                        onSuccess()
                    }

                } else {
                    isLoggedIn = false

                    val errorResponse = response.getErrorResponse<ErrorResponse>()
                    if (errorResponse != null) {
                        error = errorResponse.errors.toString()
                    }
                    saveUserPreferences(false, "", "", "", "", false)
                    Api.setCookie("")
                }

            } catch (ex: Exception) {
                error = ""
                ex.printStackTrace()

            } finally {
                isBusy = false
            }
        }
    }

    fun login(email: String, password: String) {

        if (password.isEmpty()) {
            isLoggedIn = true
            return
        }

        viewModelScope.launch {
            isBusy = true
            error = null

            try {

                val response = Api.api.login(
                    LoginRequest(
                        email,
                        password
                    )
                )


                if (response.isSuccessful) {
                    isLoggedIn = true
                    val data = response.body()

                    val cookies = response.headers().get("Set-Cookie")
                    var cookie = ""
                    if (cookies?.contains("auth_sid") == true) {
                        cookie = cookies.split("=")[1].split(";")[0]
                    }


                    if (data != null) {
                        saveUserPreferences(
                            true,
                            email,
                            data.firstname,
                            data.lastname,
                            cookie,
                            data.verified
                        )
                        user = User(data.email, data.firstname, data.lastname, data.verified)
                        Api.setCookie(cookie)
                    }

                } else {
                    isLoggedIn = false

                    val errorResponse = response.getErrorResponse<ErrorResponse>()
                    if (errorResponse != null) {
                        error = errorResponse.errors.toString()
                    }
                    saveUserPreferences(false, "", "", "", "", false)
                    Api.setCookie("")
                }

            } catch (ex: Exception) {
                error = ""
                ex.printStackTrace()

            } finally {
                isBusy = false
            }

        }
    }

    fun logout() {
        viewModelScope.launch {
            isBusy = true
            try {
                val response = Api.api.logout()

                if (response.isSuccessful) {
                    isLoggedIn = false
                    user = null
                    saveUserPreferences(false, "", "", "", "", false)
                    Api.setCookie("")
                    loadUser()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                isBusy = false
            }
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
                Api.setCookie(preferences.cookie)
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

val LocalUserState = compositionLocalOf<UserViewModel> { error("User State Context Not Found!") }
