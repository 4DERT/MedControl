package com.example.medcontrol.router


sealed class Screen(val route: String) {
    object Home: Screen(route = "home_screen")
    object Graph: Screen(route = "graph_screen")
    object Info: Screen(route = "info_screen")
}
