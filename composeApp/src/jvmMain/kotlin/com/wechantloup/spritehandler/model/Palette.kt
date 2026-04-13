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

    constructor(): this(
        colors = mutableListOf<Int>().apply {
            add(Color.Transparent.toArgb())
            (0 until 15).forEach {
                add(Color.Unspecified.toArgb())
            }
        }
    )
}
