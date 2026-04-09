package com.wechantloup.spritehandler

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.rememberNavController

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SpriteHandler",
    ) {
        val navController = rememberNavController()
        MaterialTheme {
            NavigationHost(navController)
        }
    }
}
