package com.example.medcontrol.homescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.medcontrol.R
import com.example.medcontrol.database.AppDatabase


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val m1 = MedicineViewItem(name = "Paracetamol", nextTake = "Jutro", icon = Icons.Default.AccountCircle)
    val m2 = MedicineViewItem(name = "Cirrus", nextTake = "18:30", icon = Icons.Default.AccountCircle)
    val m3 = MedicineViewItem(name = "Test", nextTake = "Jutro", icon = Icons.Default.AccountCircle)

    val meds = arrayOf(m1, m2, m3)

    // creating database
    val db by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "medicine.db"
        ).build()
    }

//    val medDao = db.medicineDao()

    Scaffold (
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
            FloatingActionButton(onClick = {  }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding),

        ) {
            items(meds) {
                MedicineCard(data = it)
            }
        }


    }

}