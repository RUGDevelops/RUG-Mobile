package eu.virtusdevelops.rug_mobile.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.virtusdevelops.datalib.models.SessionInformation
import eu.virtusdevelops.rug_mobile.domain.Result
import eu.virtusdevelops.rug_mobile.repositories.interfaces.SessionRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ActiveSessionsViewModel @Inject constructor(
    private val repository: SessionRepository
): ViewModel(){
    var isBusy by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var isLoaded by mutableStateOf(false)


    var isBusyLogingOut by mutableStateOf(false)
    var isErrorLogout by mutableStateOf(false)


    private val _sessions = MutableLiveData<List<SessionInformation>>()
    val sessions: LiveData<List<SessionInformation>> get() = _sessions

    fun load(){
        viewModelScope.launch {
            isBusy = true
            isError = false
            isLoaded = false

            loadSessions()

            isBusy = false
            isLoaded = true
        }
    }


    private suspend fun loadSessions(){
        val result = repository.getPendingSessions()

        when(result){
            is Result.Error -> {
                isError = true
                Log.e("SESSIONS", result.error.name)
            }
            is Result.Success -> {
                _sessions.value = result.data
            }
        }
    }

    fun logoutSession(sessionID: UUID){
        viewModelScope.launch {
            isBusyLogingOut = true
            isErrorLogout = false


            val result = repository.logoutSession(sessionID)

            when(result){
                is Result.Error -> {
                    isErrorLogout = true
                    Log.e("SESSIONS", result.error.name)
                }
                is Result.Success -> {
                    loadSessions()
                }
            }
            isBusyLogingOut = false
        }
    }

}