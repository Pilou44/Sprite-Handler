package com.wechantloup.spritehandler.importer

import com.wechantloup.spritehandler.model.Animation

internal object AnimationImporter {
    fun import(bytes: List<Byte>): Animation {
        val version = bytes[0].toInt()
        return when (version) {
            0 -> importV0(bytes)
            else -> throw IllegalStateException("Unknown sprite version")
        }
    }

    private fun importV0(bytes: List<Byte>): Animation {
        var index = 1
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
                offsetY = offestY
            )
            frames.add(frame)
        }
        return Animation(frames)
    }

    private fun List<Byte>.toSignedInt(): Int {
        val raw = ((this[0].toInt() and 0xFF) shl 8) or (this[1].toInt() and 0xFF)
        return if (raw >= 0x8000) raw - 0x10000 else raw
    }
}
