package com.wechantloup.spritehandler.model

data class Animation(
    val frames: List<Frame>,
) {
    data class Frame(
        val spriteFrameIndex: Int,
        val offsetX: Int,
        val offsetY: Int,
    )
}
