package com.wechantloup.spritehandler.useCase

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import com.wechantloup.spritehandler.model.Image
import com.wechantloup.spritehandler.model.Palette
import com.wechantloup.spritehandler.model.SpriteAlignment
import java.io.File
import java.util.logging.Logger

internal object SpriteUseCase {
    private val logger = Logger.getLogger("SpriteUseCase")

    fun generateSprite(
        images: List<Image>,
        palette: Palette,
        width: Int,
        height: Int,
        alignment: SpriteAlignment,
    ): List<List<Int>> {
        val images = images.filter { it.isSelected }

        val pixels: List<List<Int>> = images.map { image ->
            val bitmap = File(image.path)
                .inputStream()
                .readAllBytes()
                .decodeToImageBitmap()

            val pixelMap = bitmap.toPixelMap()

            val offsetX = when (alignment) {
                SpriteAlignment.TOP_LEFT,
                SpriteAlignment.CENTER_LEFT,
                SpriteAlignment.BOTTOM_LEFT   -> 0
                SpriteAlignment.TOP_CENTER,
                SpriteAlignment.CENTER,
                SpriteAlignment.BOTTOM_CENTER -> (width - image.width) / 2
                SpriteAlignment.TOP_RIGHT,
                SpriteAlignment.CENTER_RIGHT,
                SpriteAlignment.BOTTOM_RIGHT  -> width - image.width
            }

            val offsetY = when (alignment) {
                SpriteAlignment.TOP_LEFT,
                SpriteAlignment.TOP_CENTER,
                SpriteAlignment.TOP_RIGHT     -> 0
                SpriteAlignment.CENTER_LEFT,
                SpriteAlignment.CENTER,
                SpriteAlignment.CENTER_RIGHT  -> (height - image.height) / 2
                SpriteAlignment.BOTTOM_LEFT,
                SpriteAlignment.BOTTOM_CENTER,
                SpriteAlignment.BOTTOM_RIGHT  -> height - image.height
            }

            List(width * height) { index ->
                val outX = index % width
                val outY = index / width

                val imgX = outX - offsetX
                val imgY = outY - offsetY

                if (imgX < 0 || imgX >= image.width || imgY < 0 || imgY >= image.height) {
                    0 // zone vide → couleur transparente
                } else {
                    val color = pixelMap[imgX, imgY]
                    palette.colors.indexOf(color.toArgb())
                }
            }
        }

        return pixels
    }

    fun generatePalette(images: List<Image>): Palette {
        val colors = mutableListOf(Color.Transparent.toArgb())

        images.forEach { image ->
            val bitmap = File(image.path)
                .inputStream()
                .readAllBytes()
                .decodeToImageBitmap()

            val pixelMap = bitmap.toPixelMap()

            for (i in 0 until image.width) {
                for (j in 0 until image.height) {
                    val color = pixelMap[i, j]
                    val argbColor = color.toArgb()
                    if (!colors.contains(argbColor)) {
                        colors.add(argbColor)
                    }
                }
            }
        }

        if (colors.size > 16) throw IllegalStateException("Too many colors")

        if (colors.size < 16) {
            (colors.size until 16).forEach { _ ->
                colors.add(Color.Unspecified.toArgb())
            }
        }

        return Palette(colors)
    }

    fun isPaletteValid(images: List<Image>, palette: Palette): Boolean {
        images.forEach { image ->
            val bitmap = File(image.path)
                .inputStream()
                .readAllBytes()
                .decodeToImageBitmap()

            val pixelMap = bitmap.toPixelMap()

            for (i in 0 until image.width) {
                for (j in 0 until image.height) {
                    val color = pixelMap[i, j]
                    if (color.alpha == 1f) {
                        val argbColor = color.toArgb()
                        if (!palette.colors.contains(argbColor)) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }
}
