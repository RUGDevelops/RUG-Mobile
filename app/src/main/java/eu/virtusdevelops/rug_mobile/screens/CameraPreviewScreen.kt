package eu.virtusdevelops.rug_mobile.screens

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreviewScreen(
    navController: NavController
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var recording: Recording? = null

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text("Face Recognition")
                },
                onClick = {
                    if (recording != null) {
                        recording?.stop()
                        recording = null
                        return@ExtendedFloatingActionButton
                    }

                    val outputFile =
                        File(context.filesDir, "face_recognition.mp4") // Overrides everytime

                    recording = cameraController.startRecording(
                        FileOutputOptions.Builder(outputFile).build(),
                        AudioConfig.AUDIO_DISABLED,
                        context.mainExecutor
                    ) { event ->
                        when (event) {
                            is VideoRecordEvent.Start -> {
                                Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            is VideoRecordEvent.Finalize -> {
                                if (event.hasError()) {
                                    recording?.close()
                                    recording = null

                                    Toast.makeText(context, "Recording failed", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Recording saved to ${outputFile.absolutePath}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        recording?.stop()
                        recording = null
                    }, 5000)
                })
        }
    ) { paddingValues: PaddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(Color.BLACK)
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also { previewView ->
                    // Open front camera
                    val cameraSelector =
                        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                            .build()

                    previewView.controller = cameraController
                    cameraController.cameraSelector = cameraSelector
                    cameraController.bindToLifecycle(lifecycle)
                }
            })

    }
}

@Preview
@Composable
fun CameraPreviewScreenPreview() {
    CameraPreviewScreen(navController = rememberNavController())
}