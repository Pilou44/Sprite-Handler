package com.wechantloup.spritehandler.model

import androidx.compose.ui.graphics.Color

data class Palette(
    val colors: List<ULong>
) {
    init {
        require(colors.size == 16) { "Palette must contain 16 colors" }
        require(colors[0] == Color.Transparent.value) { "First color must be transparent" }
    }
}
