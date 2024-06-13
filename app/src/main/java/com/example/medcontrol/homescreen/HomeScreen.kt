package com.example.medcontrol.homescreen

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.example.medcontrol.homescreen.modal.MedicineModal


@SuppressLint("ScheduleExactAlarm")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    ///// TEMPORARY

//    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//    val intent = Intent(context, AlarmReceiver::class.java)
//
//    val pendingIntent = PendingIntent.getBroadcast(
//        context,
//        0,
//        intent,
//        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//    )
//
//    val calendar = Calendar.getInstance().apply {
//        set(Calendar.HOUR_OF_DAY, 19)
//        set(Calendar.MINUTE, 57)
//        set(Calendar.SECOND, 0)
//        if (before(Calendar.getInstance())) {
//            add(Calendar.DAY_OF_MONTH, 1)
//        }
//    }
//
//    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

    //////

    // creating database
    val db by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "medicine7.db"
        ).build()
    }

    val application = context.applicationContext as Application

    val viewModel = viewModel<HomeScreenViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeScreenViewModel(
                    dao = db.medicineDao(),
                    application = application
                ) as T
            }
        }
    )

    val state = viewModel.state.collectAsState()
    val fabState = viewModel.fabState.collectAsState()

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
                    listState.data,
                    onClick = { viewModel.showMedicineDetails(it) }
                )
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


@Composable
fun HomeScreenSuccess(
    contentPadding: PaddingValues,
    data: List<MedicineViewItem>,
    onClick: (MedicineViewItem) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(contentPadding),

        ) {
        items(data) {
            MedicineCard(data = it, onClick = onClick)
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
