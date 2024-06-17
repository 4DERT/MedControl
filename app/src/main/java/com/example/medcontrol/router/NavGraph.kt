package com.example.medcontrol.router

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medcontrol.graphscreen.GraphScreen
import com.example.medcontrol.homescreen.HomeScreen
import com.example.medcontrol.infoscreen.InfoScreen

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    padding: PaddingValues
) {


    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(padding)
        }

        composable(Screen.Graph.route) {
            GraphScreen(padding)
        }

        composable(Screen.Info.route) {
            InfoScreen(padding)
        }

    }

}