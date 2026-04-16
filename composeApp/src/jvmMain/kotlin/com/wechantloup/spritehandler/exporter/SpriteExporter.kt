package com.wechantloup.spritehandler.exporter

import com.wechantloup.spritehandler.model.Sprite

object SpriteExporter {

    fun exportV0(sprite: Sprite): List<Byte> {
        val bytes = mutableListOf<Byte>()

        bytes.add(0.toByte()) // Version 0

        bytes.add(sprite.width.toByte())
        bytes.add(sprite.height.toByte())

        sprite.palettes[0].colors.forEach { color ->
            val colorBytes = color.toBytes()
            bytes.addAll(colorBytes)
        }

        val imageCount = sprite.frames.size
        bytes.add(imageCount.toByte())

        val pixelBytes = sprite.frames.flatten().toPackedBytes()
        bytes.addAll(pixelBytes)

        return bytes
    }

    fun exportV1(sprite: Sprite): List<Byte> {
        val bytes = mutableListOf<Byte>()

        bytes.add(1.toByte()) // Version 1

        bytes.add(sprite.width.toByte())
        bytes.add(sprite.height.toByte())

        val paletteCount = sprite.palettes.size
        bytes.add(paletteCount.toByte())

        sprite.palettes.forEach { palette ->
            palette.colors.forEach { color ->
                val colorBytes = color.toBytes()
                bytes.addAll(colorBytes)
            }
        }

        val imageCount = sprite.frames.size
        bytes.add(imageCount.toByte())

        val pixelBytes = sprite.frames.flatten().toPackedBytes()
        bytes.addAll(pixelBytes)

        return bytes
    }

    private fun Int.toBytes(): List<Byte> = (3 downTo 0).map { i ->
        ((this shr (i * 8)) and 0xFF).toByte()
    }

    private fun List<Int>.toPackedBytes(): List<Byte> = chunked(2).map { chunk ->
        val high = chunk[0] and 0xF
        val low = if (chunk.size == 2) chunk[1] and 0xF else 0
        ((high shl 4) or low).toByte()
    }
}
