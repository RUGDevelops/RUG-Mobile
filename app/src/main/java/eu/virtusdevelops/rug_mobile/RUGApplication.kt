package eu.virtusdevelops.rug_mobile

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore by preferencesDataStore(name = "user_data")
class RUGApplication : Application() {
}