package com.example.medcontrol.homescreen

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.SwipeableState
import androidx.wear.compose.material.swipeable
import com.example.medcontrol.R
import com.example.medcontrol.database.AppDatabase
import com.example.medcontrol.homescreen.modal.MedicineModal
import com.example.medcontrol.ui.theme.playwriteFontFamily
import kotlin.math.roundToInt


@SuppressLint("ScheduleExactAlarm")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(padding: PaddingValues) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // creating database
    val db by lazy {
        Room.databaseBuilder(
            context, AppDatabase::class.java, "medicine7.db"
        ).build()
    }

    val application = context.applicationContext as Application

    val viewModel = viewModel<HomeScreenViewModel>(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeScreenViewModel(
                dao = db.medicineDao(), application = application
            ) as T
        }
    })

    val state = viewModel.state.collectAsState()
    val fabState = viewModel.fabState.collectAsState()

    val paddingValues = WindowInsets.navigationBars.asPaddingValues()

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
            FloatingActionButton(onClick = { viewModel.showAddMedicineModal() }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },

        ) { innerPadding ->

        when (val listState = state.value) {
            is ItemsListState.Loading -> HomeScreenLoading(innerPadding)

            is ItemsListState.Empty -> HomeScreenEmpty(innerPadding)

            is ItemsListState.Success -> HomeScreenSuccess(innerPadding,
                listState.data,
                onClick = { viewModel.showMedicineDetails(it) },
                onDelete = { viewModel.deleteMedicine(it) })
        }

        if (fabState.value.isAddMedicineModalVisible) {
            MedicineModal(
                onDismissRequest = { viewModel.dismissAddMedicineModal() },
                onConfirm = { viewModel.addMedicine(it) },
                onUpdate = { viewModel.updateMedicine(it) },
                onHideDialog = { viewModel.hideAddMedicineModal() },
                data = fabState.value.medicineToEdit
            )
        }
    }


}


@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun HomeScreenSuccess(
    contentPadding: PaddingValues,
    data: List<MedicineViewItem>,
    onClick: (MedicineViewItem) -> Unit,
    onDelete: (MedicineViewItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(contentPadding),
    ) {
        items(data) { item ->
            val swipeableState = SwipeableState(initialValue = 0)
            val sizePx = with(LocalDensity.current) { 72.dp.toPx() }
            val anchors = mapOf(0f to 0, -sizePx to 1)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
                        orientation = Orientation.Horizontal
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .align(Alignment.CenterEnd)
                        .padding(10.dp)
                ) {
                    IconButton(
                        onClick = {
                            onDelete(item)
                        }, modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                        )
                    }
                }
                Box(modifier = Modifier
                    .offset {
                        IntOffset(
                            swipeableState.offset.value.roundToInt(), 0
                        )
                    }
                    .fillMaxWidth()
                    .background(Color.Transparent)) {
                    MedicineCard(data = item, onClick = onClick)
                }
            }
        }
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
            text = stringResource(id = R.string.empty_list_emoji),
            modifier = Modifier.rotate(90f)
        )

        Text(
            modifier = Modifier.padding(start = 32.dp, end = 32.dp),
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
