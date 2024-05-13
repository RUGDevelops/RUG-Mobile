package com.example.rug_mobile

import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rug_mobile.ui.theme.RUGMobileTheme
import io.github.g00fy2.quickie.ScanQRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController
) {
    var presses by remember { mutableIntStateOf(0) }
    var scanQRCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        result.let {
            var qrData = result.toString()
            Log.d("RESULT_DATA", qrData)
            parseQrResult(qrData)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Home supply station")
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { presses++ }) {
                Icon(Icons.Default.List, contentDescription = "History")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = {
                scanQRCodeLauncher.launch(null)
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )) {
                Text("Scan qr code")
            }
        }
    }
}

private fun playBase64Wav(base64Wav: String) {
    // Decode the Base64 string to a byte array
    val decodedWav = Base64.decode(base64Wav, Base64.DEFAULT)

    // Write the byte array to a temporary .zip file
    val tempZipFile = File.createTempFile("temp", ".zip")
    val fos = FileOutputStream(tempZipFile)
    fos.write(decodedWav)
    fos.close()

    // Unzip the .zip file to extract the .wav file
    val zis = ZipInputStream(tempZipFile.inputStream())
    val entry = zis.nextEntry
    val tempWavFile = File.createTempFile("temp", ".wav")
    tempWavFile.outputStream().use { it.write(zis.readBytes()) }

    // Use a MediaPlayer to play the .wav file
    val mediaPlayer = MediaPlayer()
    mediaPlayer.setDataSource(tempWavFile.absolutePath)
    mediaPlayer.prepare()
    mediaPlayer.start()
}

private fun makeApiRequest(box: Box) {
    println(box)
    val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getUnsafeOkHttpClient())
        .build()
        .create(ApiRequest::class.java)

    GlobalScope.launch(Dispatchers.IO) {
        try {
            val response = api.getApiData(box)
            Log.d("API_DATA", response.data) // Print to console
            playBase64Wav(response.data) // Play the audio

            withContext(Dispatchers.Main) {
                //Toast.makeText(this@MainActivity, "Playing sound...", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.d(
                "API_ERROR",
                "Failed to make API request or play file, with error: ${e.message}, box: $box"
            ) // Print error to console
        }
    }
}

private fun parseQrResult(qrData: String) {
    val rawValueStartIndex = qrData.indexOf("rawValue=") + "rawValue=".length
    val rawValueEndIndex = qrData.indexOf(")", startIndex = rawValueStartIndex)
    val rawValue = qrData.substring(rawValueStartIndex, rawValueEndIndex)

    val components = rawValue.split(",")

    if (components.size == 3) {
        val url = components[0]
        val urlComponents = url.split("/")

        if (urlComponents.size >= 11) {
            val deliveryId = urlComponents[3].toIntOrNull() ?: 0
            val boxId = urlComponents[4].toIntOrNull() ?: 0
            val tokenFormat = urlComponents[5].toIntOrNull() ?: 0
            val latitude = urlComponents[6].toIntOrNull() ?: 0
            val longitude = urlComponents[7].toIntOrNull() ?: 0
            val terminalSeed = urlComponents[8].toIntOrNull() ?: 0
            val doorIndex = urlComponents[9].toIntOrNull() ?: 0

            for (component in urlComponents) {
                Log.d("URL_COMPONENT", component)
            }

            makeApiRequest(
                Box(
                    deliveryId = deliveryId,
                    boxId = boxId.toString().trimStart('0').toInt(),
                    tokenFormat = 2,
                    latitude = latitude,
                    longitude = longitude,
                    qrCodeInfo = "string",
                    terminalSeed = terminalSeed,
                    isMultibox = false,
                    doorIndex = doorIndex,
                    addAccessLog = true
                )
            )
        } else {
            Log.d("QR_ERROR", "Failed to parse QR code data: $qrData")
        }
    } else {
        Log.d("QR_ERROR", "Failed to parse QR code data: $qrData")
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    RUGMobileTheme {
        MainScreen(navController = rememberNavController())
    }
}