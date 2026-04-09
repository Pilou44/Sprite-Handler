package com.wechantloup.spritehandler

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SpriteHandler",
    ) {
        App()
    }
}