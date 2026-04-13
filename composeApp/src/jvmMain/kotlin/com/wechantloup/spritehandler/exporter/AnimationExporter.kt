package com.wechantloup.spritehandler.exporter

import com.wechantloup.spritehandler.model.Animation

internal object AnimationExporter {
    fun export(animation: Animation): List<Byte> {
        val bytes = mutableListOf<Byte>()

        bytes.add(0.toByte()) // Version 0

        val frameCount = animation.frames.size
        require(frameCount <= 255) { "Animation frame count must be 255 or less" }
        bytes.add(frameCount.toByte())

        animation.frames.forEach { frame ->
            val index = frame.spriteFrameIndex
            require(index <= 255) { "Sprite frame count must be 255 or less" }
            bytes.add(index.toByte())

            val offsetX = frame.offsetX
            require(offsetX <= 255) { "Horizontal offset frame count must be 255 or less" }
            require(offsetX >= -255) { "Horizontal offset frame count must be -255 or more" }
            bytes.addAll(offsetX.toSignedBytes())

            val offsetY = frame.offsetY
            require(offsetY <= 255) { "Vertical offset frame count must be 255 or less" }
            require(offsetY >= -255) { "Vertical offset frame count must be -255 or more" }
            bytes.addAll(offsetY.toSignedBytes())
        }
        return bytes
    }

    fun Int.toSignedBytes(): List<Byte> = listOf(
        (this shr 8).toByte(),
        (this and 0xFF).toByte()
    )
}
