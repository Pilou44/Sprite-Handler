package com.wechantloup.spritehandler.model

data class Animation(
    val frames: List<Frame>,
    val width: Int,
    val height: Int,
) {
    val isValid: Boolean
        get() = frames.isNotEmpty() && width > 0 && height > 0

    init {
        require(frames.size <= 255) { "Frame count must be 255 or less" }
        require(width <= 255) { "Width must be 255 or less" }
        require(height <= 255) { "Height must be 255 or less" }
    }

    data class Frame(
        val spriteFrameIndex: Int,
        val offsetX: Int,
        val offsetY: Int,
        val paletteIndex: Int,
        val durationMs: Int = 100,
        val brightness: Float = 1f,
    ) {
        init {
            require(paletteIndex <= 255) { "Palette index must be 255 or less" }
            require(durationMs >= 40) { "Duration must be 40ms or more" }
        }
    }
}
