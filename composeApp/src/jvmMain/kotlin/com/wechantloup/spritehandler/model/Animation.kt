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
        val durationMs: Int,
        val brightness: Float,
        val isHorizontallyMirrored: Boolean,
        val isVerticallyMirrored: Boolean,
    ) {
        init {
            require(paletteIndex <= 255) { "Palette index must be 255 or less" }
            require(durationMs >= 40) { "Duration must be 40 ms or more" }
            require(durationMs <= 255) { "Duration must be 255 ms or less" }
            require(brightness <= 1f) { "Brightness must be between 0 and 1" }
            require(brightness >= 0f) { "Brightness must be between 0 and 1" }
        }
    }
}
