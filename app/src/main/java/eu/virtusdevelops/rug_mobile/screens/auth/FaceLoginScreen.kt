package eu.virtusdevelops.rug_mobile.screens.auth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.rug_mobile.navigation.AuthGraph
import eu.virtusdevelops.rug_mobile.navigation.Graph
import eu.virtusdevelops.rug_mobile.screens.GradientBackground
import eu.virtusdevelops.rug_mobile.screens.GradientCard
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel


@Composable
fun FaceLoginScreen(
    navController: NavController,
    innerPaddingValues: PaddingValues
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val viewModel = hiltViewModel<UserViewModel>()

    val modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth()

    isError = username.isEmpty() && password.isEmpty()


    val context = LocalContext.current

    var controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }
    }



    GradientBackground (

    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
            .systemBarsPadding()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //make bold text with different font


                GradientCard(
                    onClick =  {},
                    modifier = Modifier
                        .padding(16.dp)
                        .height(450.dp)
                        .fillMaxWidth()
                ){
                    CameraPreview(
                        controller = controller,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                    )
                }
                


                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Email") },
                    placeholder = { Text("Your email here") },
                    singleLine = true,
                    modifier = modifier,
                    isError = isError,
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = "Username")
                    })

                if (isError) {
                    Text(
                        "Fields must not be empty",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

//            TODO: add camera open shit and save image watever kura

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    shape = RoundedCornerShape(10.dp),
                    onClick = {

                        takePhoto(
                            context,
                            controller = controller,
                            onPhotoTaken = {
                                viewModel.loginWithImage(
                                    username,
                                    it
                                )
                            }
                        )

                    },
                    modifier = modifier
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary)) {

                    if(viewModel.isBusy){
                        CircularProgressIndicator()
                    }else if(viewModel.isLoggedIn){
                        navController.navigate(Graph.HOME) {
                            popUpTo(navController.graph.id)
                        }
                    }else{
                        Text("Login")
                    }

                }

                ClickableText(
                    modifier = Modifier.padding(9.dp),
                    style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                    text = AnnotatedString("No face login? Click here"),
                ) {
                    navController.navigate(AuthGraph.LoginScreen.route){
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                ClickableText(
                    modifier = Modifier.padding(9.dp),
                    style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                    text = AnnotatedString("Don't have an account? Register"),
                ) {
                    navController.navigate(AuthGraph.RegisterScreen.route){
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }
    }



}

private fun takePhoto(
    applicationContext: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(applicationContext),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                Log.i("Camera", "Took new photo!")

                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        }
    )
}


@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}

