package com.wechantloup.spritehandler.importer

import com.wechantloup.spritehandler.model.Animation
import kotlin.math.roundToInt

internal object AnimationImporter {
    fun import(bytes: List<Byte>): Animation {
        val version = bytes[0].toInt() and 0xFF
        return when (version) {
            0 -> importV0(bytes)
            1 -> importV1(bytes)
            2 -> importV2(bytes)
            else -> throw IllegalStateException("Unknown sprite version")
        }
    }

    private fun importV0(bytes: List<Byte>): Animation {
        var index = 1

        val width = bytes[index++].toInt() and 0xFF
        val height = bytes[index++].toInt() and 0xFF

        val frameCount = bytes[index++].toInt() and 0xFF

        val frames = mutableListOf<Animation.Frame>()
        for (i in 0 until frameCount) {
            val spriteFrameIndex = bytes[index + i * 5].toInt() and 0xFF
            val offsetXBytes = bytes.subList(index + 1 + i * 5, index + 3 + i * 5)
            val offsetX = offsetXBytes.toSignedInt()
            val offsetYBytes = bytes.subList(index + 3 + i * 5, index + 5 + i * 5)
            val offestY = offsetYBytes.toSignedInt()
            val frame = Animation.Frame(
                spriteFrameIndex = spriteFrameIndex,
                offsetX = offsetX,
                offsetY = offestY,
                paletteIndex = 0,
                durationMs = 100,
                brightness = 1f,
                isHorizontallyMirrored = false,
                isVerticallyMirrored = false,
            )
            frames.add(frame)
        }
        return Animation(frames, width, height)
    }

    private fun importV1(bytes: List<Byte>): Animation {
        var index = 1

        val width = bytes[index++].toInt() and 0xFF
        val height = bytes[index++].toInt() and 0xFF

        val frameCount = bytes[index++].toInt() and 0xFF

        val frames = mutableListOf<Animation.Frame>()
        for (i in 0 until frameCount) {
            val spriteFrameIndex = bytes[index + i * 6].toInt() and 0xFF
            val offsetXBytes = bytes.subList(index + 1 + i * 6, index + 3 + i * 6)
            val offsetX = offsetXBytes.toSignedInt()
            val offsetYBytes = bytes.subList(index + 3 + i * 6, index + 5 + i * 6)
            val offestY = offsetYBytes.toSignedInt()
            val paletteIndex = bytes[index + 5 + i * 6].toInt() and 0xFF
            val frame = Animation.Frame(
                spriteFrameIndex = spriteFrameIndex,
                offsetX = offsetX,
                offsetY = offestY,
                paletteIndex = paletteIndex,
                durationMs = 100,
                brightness = 1f,
                isHorizontallyMirrored = false,
                isVerticallyMirrored = false,
            )
            frames.add(frame)
        }
        return Animation(frames, width, height)
    }

    private fun importV2(bytes: List<Byte>): Animation {
        var index = 1

        val width = bytes[index++].toInt() and 0xFF
        val height = bytes[index++].toInt() and 0xFF

        val frameCount = bytes[index++].toInt() and 0xFF

        val frames = mutableListOf<Animation.Frame>()
        for (i in 0 until frameCount) {
            val spriteFrameIndex = bytes[index + i * 8].toInt() and 0xFF
            val offsetXBytes = bytes.subList(index + 1 + i * 8, index + 3 + i * 8)
            val offsetX = offsetXBytes.toSignedInt()
            val offsetYBytes = bytes.subList(index + 3 + i * 8, index + 5 + i * 8)
            val offestY = offsetYBytes.toSignedInt()
            val paletteIndex = bytes[index + 5 + i * 8].toInt() and 0xFF
            val durationMs = bytes[index + 6 + i * 8].toInt() and 0xFF
            val brightnessByte = bytes[index + 7 + i * 8]
            val brightness = ((brightnessByte.toInt() and 0xFF) / 255f * 100).roundToInt() / 100f
            val frame = Animation.Frame(
                spriteFrameIndex = spriteFrameIndex,
                offsetX = offsetX,
                offsetY = offestY,
                paletteIndex = paletteIndex,
                durationMs = durationMs,
                brightness = brightness,
                isHorizontallyMirrored = false, // ToDo
                isVerticallyMirrored = false, // ToDo
            )
            frames.add(frame)
        }
        return Animation(frames, width, height)
    }

    private fun List<Byte>.toSignedInt(): Int {
        val raw = ((this[0].toInt() and 0xFF) shl 8) or (this[1].toInt() and 0xFF)
        return if (raw >= 0x8000) raw - 0x10000 else raw
    }
}
