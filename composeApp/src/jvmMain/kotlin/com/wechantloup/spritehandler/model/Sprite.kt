package com.wechantloup.spritehandler.model

data class Sprite(
    val width: Int,
    val height: Int,
    val palette: Palette,
    val frames: List<List<Int>>,
)
