package eu.virtusdevelops.rug_mobile.services

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.backendapi.requests.ChangeDeviceTokenRequest
import eu.virtusdevelops.rug_mobile.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PushNotificationService: FirebaseMessagingService() {


    private val DEVICE_TOKEN = stringPreferencesKey("device_token")

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)


    @Inject
    lateinit var apiRequest: ApiRequest




    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.i("FIREBASE", "Yo new token for device has been given: $token")



        // send this token to backend to update it
        scope.launch{
            // get current token
            val currentToken = getCurrentToken(dataStore)
            saveCurrentToken(token, dataStore)

            if(currentToken != null){
                apiRequest.updateDeviceToken(currentToken, ChangeDeviceTokenRequest(token))
            }

        }
    }




    private suspend fun saveCurrentToken(token: String, dataStore: DataStore<Preferences>){
        dataStore.edit {
            it[DEVICE_TOKEN] = token
        }
    }

    private suspend fun getCurrentToken(dataStore: DataStore<Preferences>): String? {
        try {

            return dataStore.data.map {
                it[DEVICE_TOKEN]
            }.firstOrNull()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}