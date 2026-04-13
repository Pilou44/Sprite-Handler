package com.wechantloup.spritehandler.importer

import com.wechantloup.spritehandler.model.Palette
import com.wechantloup.spritehandler.model.Sprite

internal object SpriteImporter {

    fun import(bytes: List<Byte>): Sprite {
        val version = bytes[0].toInt()
        return when (version) {
            0 -> importV0(bytes)
            else -> throw IllegalStateException("Unknown sprite version")
        }
    }

    private fun importV0(bytes: List<Byte>): Sprite {
        var index = 1
        val width = bytes[index++].toInt() and 0xFF
        val height = bytes[index++].toInt() and 0xFF
        val colors = (0 until 16).map { i ->
            val byteColor = bytes.subList(index + 4 * i, index + 4 * (i + 1))
            byteColor.toInt()
        }
        index += 4 * 16
        val palette = Palette(colors)

        val imageCount = bytes[index++].toInt() and 0xFF

        val imageSize = width * height
        val pixelCount = imageSize * imageCount
        val pixelBytes = bytes.subList(index, bytes.size)
        val pixels = pixelBytes.toNibbles(pixelCount)
        val frames = (0 until imageCount).map {
            pixels.subList(it * imageSize, (it + 1) * imageSize)
        }

        return Sprite(
            width = width,
            height = height,
            palette = palette,
            frames = frames,
        )
    }

    private fun List<Byte>.toInt(): Int = fold(0) { acc, byte ->
        (acc shl 8) or (byte.toInt() and 0xFF)
    }

    private fun List<Byte>.toNibbles(originalSize: Int): List<Int> = flatMap { byte ->
        val high = (byte.toInt() shr 4) and 0xF
        val low = byte.toInt() and 0xF
        listOf(high, low)
    }.take(originalSize)
}
