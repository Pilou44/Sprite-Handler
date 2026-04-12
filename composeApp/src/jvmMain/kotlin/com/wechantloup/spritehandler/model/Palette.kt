package com.wechantloup.spritehandler.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class Palette(
    val colors: List<Int>
) {
    init {
        require(colors.size == 16) { "Palette must contain 16 colors" }
        require(colors[0] == Color.Transparent.toArgb()) { "First color must be transparent" }
    }
}
