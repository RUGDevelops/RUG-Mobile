package eu.virtusdevelops.rug_mobile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import eu.virtusdevelops.backendapi.Api
import eu.virtusdevelops.backendapi.requests.LoginRequest
import eu.virtusdevelops.backendapi.responses.ErrorResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun LoginScreen(
    navController: NavController
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val modifier = Modifier.padding(5.dp)

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //make bold text with different font
        Text(text = "LOGIN", modifier = modifier
            .padding(10.dp)
            .padding(5.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = (modifier.fillMaxSize(0.1f)))
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = modifier,
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = "Username")
            })
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = modifier,
            leadingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(Icons.Filled.Lock, contentDescription = "Toggle password visibility")
                }
            }
        )
        Spacer(modifier = modifier.fillMaxSize(0.7f))
        val context = LocalContext.current
        Button(
            onClick = {
                // Development mode bypass.
                if(password.isEmpty()){
                    navController.navigate(Screen.MainScreen.route)
                    return@Button
                }


                GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                    val response = Api.api.login(LoginRequest(
                        username,
                        password
                    ))

                    if(response.isSuccessful){
                        // save user into app state

                        withContext(Dispatchers.Main){
                            navController.navigate(Screen.MainScreen.route)
                        }
                    }else{
//                        val gson = Gson()
//                        // display error
//                        val errorResponse = gson.fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java)
//
//
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, response.code().toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                }







            },
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary)) {
            Text("Login")
        }
    }
}


val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
    throwable.printStackTrace()
}




@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController())
}