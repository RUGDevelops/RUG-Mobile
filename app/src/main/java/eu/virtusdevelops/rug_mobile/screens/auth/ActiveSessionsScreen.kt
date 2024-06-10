package eu.virtusdevelops.rug_mobile.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import eu.virtusdevelops.datalib.models.SessionInformation
import eu.virtusdevelops.rug_mobile.R
import eu.virtusdevelops.rug_mobile.screens.GradientCard
import eu.virtusdevelops.rug_mobile.viewModels.ActiveSessionsViewModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActiveSessionsList(navController: NavController, innerPaddingValues: PaddingValues){


    val viewModel = hiltViewModel<ActiveSessionsViewModel>()
    val sessions by viewModel.sessions.observeAsState(emptyList())

    val isBusy by remember { viewModel::isBusy }
    val isError by remember { viewModel::isError }


    LaunchedEffect(Unit) {
        if(!viewModel.isLoaded)
            viewModel.load()
    }



    val refreshState = rememberPullRefreshState(
        refreshing = isBusy,
        onRefresh = { viewModel.load() }
    )


    Box(
        modifier = Modifier
            .pullRefresh(refreshState)
            .fillMaxHeight()
            .padding(innerPaddingValues)
    ){

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = "Active sessions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isError) {
                Text(text = "An error occurred. Please try again.")
            } else if(!isBusy){
                if(sessions.isEmpty()){
                    Text(text = "No sessions found")
                }else{



                    ListAllActiveSessions(sessions = sessions, viewModel)
                }
            }
        }
        

    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pullRefresh(refreshState),
        contentAlignment = Alignment.TopCenter){

        PullRefreshIndicator(
            refreshing = isBusy,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

}

@Composable
fun ListAllActiveSessions(sessions: List<SessionInformation>, viewModel: ActiveSessionsViewModel){



    GradientCard(
        modifier = Modifier.padding(bottom = 30.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        onClick = {}) {
        LazyColumn {
            sessions.forEach {
                item{
                    ActiveSessionCard(session = it, viewModel)
                    HorizontalDivider(thickness = 0.5.dp)
                }
            }
        }
    }


}



@Composable
fun ActiveSessionCard(session: SessionInformation, viewModel: ActiveSessionsViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.ip,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = session.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = {
            // TODO: kick session
            viewModel.logoutSession(session.id)
        }) {
            Icon(
                painter = painterResource(R.drawable.x_solid),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(46.dp)
            )
        }
    }
}
