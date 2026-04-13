package com.wechantloup.spritehandler.model

data class Animation(
    val frames: List<Frame>,
) {
    init {
        require(frames.size <= 255) { "Frame count must be 255 or less" }
    }

    data class Frame(
        val spriteFrameIndex: Int,
        val offsetX: Int,
        val offsetY: Int,
    )
}
