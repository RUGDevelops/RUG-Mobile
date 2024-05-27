package eu.virtusdevelops.rug_mobile

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp


val Context.dataStore by preferencesDataStore(name = "user_data")


@HiltAndroidApp
class RUGApplication : Application()