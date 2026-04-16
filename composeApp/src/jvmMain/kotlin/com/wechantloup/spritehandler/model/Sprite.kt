package com.wechantloup.spritehandler.model

data class Sprite(
    val width: Int,
    val height: Int,
    val palettes: List<Palette>,
    val frames: List<List<Int>>,
) {
    init {
        require(width <= 255) { "Width must be 255 or less" }
        require(height <= 255) { "Width must be 255 or less" }
        require(palettes.size <= 255) { "Palette count must be 255 or less" }
        require(frames.size <= 255) { "Frame count must be 255 or less" }
    }
}
