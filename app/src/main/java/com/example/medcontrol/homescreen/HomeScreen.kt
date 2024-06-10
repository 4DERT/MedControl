package com.example.medcontrol.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.medcontrol.R
import com.example.medcontrol.database.AppDatabase
import com.example.medcontrol.database.Medicine


data class HomeScreenViewItem(
    val isAddMedicineModalVisible: Boolean,
    val medicineList: List<MedicineViewItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // creating database
    val db by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "medicine1.db"
        ).build()
    }

    val viewModel = viewModel<HomeScreenViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeScreenViewModel(
                    dao = db.medicineDao(),
                ) as T
            }
        }
    )
    val state = viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = context.getString(R.string.app_name))
                },
                scrollBehavior = scrollBehavior
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddMedicineModal() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

    ) { innerPadding ->

        when (val listState = state.value) {
            is ItemsListState.Loading ->
                HomeScreenLoading(innerPadding)

            is ItemsListState.Empty ->
                HomeScreenEmpty(innerPadding)

            is ItemsListState.Success ->
                HomeScreenSuccess(
                    innerPadding,
                    listState.data.medicineList,
                    listState.data.isAddMedicineModalVisible,
                    onAddMedicineDismiss = { viewModel.dismissAddMedicineModal() },
                    onAddMedicineConfirm = {  }
                )
        }


    }


}


@Composable
fun HomeScreenSuccess(
    contentPadding: PaddingValues,
    data: List<MedicineViewItem>,
    isAddMedicineModalVisible: Boolean,
    onAddMedicineDismiss: () -> Unit = {},
    onAddMedicineConfirm: (medicine: AddMedicineViewItem) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier
            .padding(contentPadding),

        ) {
        items(data) {
            MedicineCard(data = it)
        }
    }

    if (isAddMedicineModalVisible) {
        AddMedicineModal(
            onDismissRequest = onAddMedicineDismiss,
            onConfirm = onAddMedicineConfirm
        )
    }
}

@Composable
fun HomeScreenEmpty(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            fontSize = 68.sp,
            fontWeight = FontWeight.Bold,
            text = ":(",
            modifier = Modifier.rotate(90f)
        )

        Text(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.empty_list)
        )

    }

}

@Composable
fun HomeScreenLoading(contentPadding: PaddingValues) {
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
