package eu.virtusdevelops.rug_mobile.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.virtusdevelops.datalib.models.SessionInformation
import eu.virtusdevelops.rug_mobile.repositories.SessionsRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PendingSessionsViewModel @Inject constructor(
    private val repository: SessionsRepository
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
            try{
                val sessions = repository.getPendingSessions()
                _sessions.value = sessions
            }catch (ex :Exception){
                isError = true
                ex.printStackTrace()
            }finally {
                isBusy = false
                isLoaded = true
            }
        }
    }


    fun approveSession(sessionID: UUID){
        viewModelScope.launch {
            isBusyLogingOut = true
            isErrorLogout = false
            try{
                val response = repository.approveSession(sessionID)
                if(response){
                    isErrorLogout = false

                    //
                    val sessions = repository.getPendingSessions()
                    _sessions.value = sessions


                }else{
                    isErrorLogout = true
                }



            }catch (ex: Exception){
                isErrorLogout = true
            }finally {
                isBusyLogingOut = false
            }
        }
    }

    fun declineSession(sessionID: UUID){
        viewModelScope.launch {
            isBusyLogingOut = true
            isErrorLogout = false
            try{
                val response = repository.declineSession(sessionID)
                if(response){
                    isErrorLogout = false

                    //
                    val sessions = repository.getPendingSessions()
                    _sessions.value = sessions


                }else{
                    isErrorLogout = true
                }



            }catch (ex: Exception){
                isErrorLogout = true
            }finally {
                isBusyLogingOut = false
            }
        }
    }


}