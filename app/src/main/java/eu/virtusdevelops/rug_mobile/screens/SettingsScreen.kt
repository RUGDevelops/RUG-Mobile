package eu.virtusdevelops.rug_mobile.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.rug_mobile.R
import eu.virtusdevelops.rug_mobile.navigation.Graph
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, innerPaddingValues: PaddingValues) {

    val viewModel = hiltViewModel<UserViewModel>()


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Settings",
                        fontWeight = FontWeight.Bold,)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon =  {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padValues ->
        Column(
            modifier = Modifier
                .padding(padValues)
                .fillMaxSize()
        ) {


            GradientCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = {}
            ) {

                Column(modifier = Modifier.padding(8.dp)) {


                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            // open help menu?
                        }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Help",
                            style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, background = Color.Transparent, textAlign = TextAlign.Left)
                        )
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            // todo: open change pass menu
                        }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Change password",
                            style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, background = Color.Transparent, textAlign = TextAlign.Left)
                        )
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            // todo: open sessions menu
                        }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Sessions",
                            style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, background = Color.Transparent, textAlign = TextAlign.Left)
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(16.dp))



            GradientCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = {}
            ) {

                Column(modifier = Modifier.padding(8.dp)) {

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            // todo: open sessions menu
                        }) {
                        Icon(
                            painterResource(id = R.drawable.question_solid),
                            modifier = Modifier.size(26.dp),
                            contentDescription = "About",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "About",
                            style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, background = Color.Transparent, textAlign = TextAlign.Left)
                        )
                    }




                        
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            viewModel.logout {
                                navController.navigate(Graph.AUTHENTICATION) {
                                    popUpTo(navController.graph.id)
                                }
                            }
                        }) {
                        Icon(
                            painterResource(id = R.drawable.right_from_bracket_solid),
                            modifier = Modifier.size(26.dp),
                            contentDescription = "Logout icon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Logout",
                            style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, background = Color.Transparent, textAlign = TextAlign.Left)
                        )
                    }


                }
            }
        }
    }





}

