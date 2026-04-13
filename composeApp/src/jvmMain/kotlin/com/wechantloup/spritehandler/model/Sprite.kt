package com.wechantloup.spritehandler.model

data class Sprite(
    val width: Int,
    val height: Int,
    val palette: Palette,
    val frames: List<List<Int>>,
) {
    init {
        require(width <= 255) { "Width must be 255 or less" }
        require(height <= 255) { "Width must be 255 or less" }
        require(frames.size <= 255) { "Frame count must be 255 or less" }
    }
}
