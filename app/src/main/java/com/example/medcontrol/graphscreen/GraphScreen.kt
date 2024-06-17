package com.example.medcontrol.graphscreen

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medcontrol.R
import com.example.medcontrol.graphdatabase.GraphDatabase
import com.example.medcontrol.graphscreen.modal.GraphModal
import com.example.medcontrol.ui.theme.playwriteFontFamily
import com.github.mikephil.charting.data.Entry

data class GraphScreenViewItem(
    val isModalVisible: Boolean,
)

data class GraphDataViewItem(
    val hearthRateData: GraphData,
    val bloodSugarData: GraphData,
    val bloodPressureSystolicData: GraphData,
    val bloodPressureDiastolicData: GraphData
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(padding: PaddingValues) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel = viewModel<GraphScreenViewModel>(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GraphScreenViewModel(
                graphDao = GraphDatabase.getDatabase(context).graphDao(),
                application = application
            ) as T
        }
    })

    val state = viewModel.state.collectAsState()
    val fabState = viewModel.fabState.collectAsState()

    val paddingValues = WindowInsets.navigationBars.asPaddingValues()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(
                bottom = padding.calculateBottomPadding() - paddingValues.calculateBottomPadding()
            ),

        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontFamily = playwriteFontFamily
                    )
                }, scrollBehavior = scrollBehavior
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.showModal()
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },

        ) { innerPadding ->

        when (val listState = state.value) {
            is GraphScreenState.Loading ->
                GraphScreenLoading(innerPadding)

            is GraphScreenState.Success -> {
                GraphScreenSuccess(innerPadding, listState.data)
            }

            is GraphScreenState.Empty ->
                GraphScreenEmpty(innerPadding)
        }

        if (fabState.value.isModalVisible)
            GraphModal(
                onHideDialog = {
                    viewModel.hideModal()
                },
                onDismissRequest = {
                    viewModel.hideModal()
                },
                onConfirm = { data ->
                    viewModel.insertData(data)
                }
            )

    }
}

@Composable
fun GraphScreenSuccess(
    innerPadding: PaddingValues,
    graphData: GraphDataViewItem
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        if (graphData.hearthRateData.entries.size > 1) {
            GraphCard(
                title = "Heart Rate",
                chartData = graphData.hearthRateData
            )
        }
        if (graphData.bloodSugarData.entries.size > 1) {
            GraphCard(
                title = "Blood Sugar",
                chartData = graphData.bloodSugarData
            )
        }
//        if (graphData.bloodPressureSystolicData.size > 1 &&
//            graphData.bloodPressureDiastolicData.size > 1
//        ) {
//            GraphCard(
//                title = "Blood Pressure Systolic",
//                chartData = graphData.bloodPressureSystolicData
//            )
//        }
    }

}


@Composable
fun GraphScreenEmpty(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(start = 32.dp, end = 32.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.graph_screen_empty)
        )
    }

}

@Composable
fun GraphScreenLoading(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}