package com.wechantloup.spritehandler.exporter

import com.wechantloup.spritehandler.model.Animation
import kotlin.math.roundToInt

internal object AnimationExporter {
    fun exportV0(animation: Animation): List<Byte> {
        val bytes = mutableListOf<Byte>()

        bytes.add(0.toByte()) // Version 0

        bytes.add(animation.width.toByte())
        bytes.add(animation.height.toByte())

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

    fun exportV1(animation: Animation): List<Byte> {
        val bytes = mutableListOf<Byte>()

        bytes.add(1.toByte()) // Version 1

        bytes.add(animation.width.toByte())
        bytes.add(animation.height.toByte())

        val frameCount = animation.frames.size
        require(frameCount <= 255) { "Animation frame count must be 255 or less" }
        bytes.add(frameCount.toByte())

        animation.frames.forEach { frame ->
            val index = frame.spriteFrameIndex
            require(index <= 255) { "Sprite frame count must be 255 or less" }
            bytes.add(index.toByte())

            val offsetX = frame.offsetX
            require(offsetX <= 255) { "Horizontal offset must be 255 or less" }
            require(offsetX >= -255) { "Horizontal offset must be -255 or more" }
            bytes.addAll(offsetX.toSignedBytes())

            val offsetY = frame.offsetY
            require(offsetY <= 255) { "Vertical offset must be 255 or less" }
            require(offsetY >= -255) { "Vertical offset must be -255 or more" }
            bytes.addAll(offsetY.toSignedBytes())

            val paletteIndex = frame.paletteIndex
            require(paletteIndex <= 255) { "Palette index must be 255 or less" }
            bytes.add(paletteIndex.toByte())
        }
        return bytes
    }

    fun exportV2(animation: Animation): List<Byte> {
        val bytes = mutableListOf<Byte>()

        bytes.add(2.toByte()) // Version 2

        bytes.add(animation.width.toByte())
        bytes.add(animation.height.toByte())

        val frameCount = animation.frames.size
        require(frameCount <= 255) { "Animation frame count must be 255 or less" }
        bytes.add(frameCount.toByte())

        animation.frames.forEach { frame ->
            val index = frame.spriteFrameIndex
            require(index <= 255) { "Sprite frame count must be 255 or less" }
            bytes.add(index.toByte())

            val offsetX = frame.offsetX
            require(offsetX <= 255) { "Horizontal offset must be 255 or less" }
            require(offsetX >= -255) { "Horizontal offset must be -255 or more" }
            bytes.addAll(offsetX.toSignedBytes())

            val offsetY = frame.offsetY
            require(offsetY <= 255) { "Vertical offset must be 255 or less" }
            require(offsetY >= -255) { "Vertical offset must be -255 or more" }
            bytes.addAll(offsetY.toSignedBytes())

            val paletteIndex = frame.paletteIndex
            require(paletteIndex <= 255) { "Palette index must be 255 or less" }
            bytes.add(paletteIndex.toByte())

            val durationMs = frame.durationMs
            require(durationMs <= 255) { "Duration must be 255 or less" }
            bytes.add(durationMs.toByte())

            val brightnessByte = (frame.brightness * 255).roundToInt().toByte()
            bytes.add(brightnessByte)
        }
        return bytes
    }

    fun Int.toSignedBytes(): List<Byte> = listOf(
        (this shr 8).toByte(),
        (this and 0xFF).toByte()
    )
}
