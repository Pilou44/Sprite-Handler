package com.wechantloup.spritehandler.importer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.wechantloup.spritehandler.exporter.SpriteExporter
import com.wechantloup.spritehandler.model.Palette
import com.wechantloup.spritehandler.model.Sprite
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class SpriteImporterTest {

    @Test
    fun checkSpriteExportImportV0() {
        val sourcePalette = Palette(
            colors = listOf(
                Color.Transparent,
                Color.Green,
                Color.Red,
                Color.Blue,
                Color.Cyan,
                Color.Yellow,
                Color.Magenta,
                Color.White,
                Color.Black,
                Color.LightGray,
                Color.Gray,
                Color.DarkGray,
                Color(128, 64, 234),
                Color(84, 65, 248),
                Color(20, 129, 212),
                Color(238, 178, 98),
            ).map {
                it.toArgb()
            }
        )
        val width = 13
        val height = 13

        val frame1 = Random(seed = 42).let { r -> List(width * height) { r.nextInt(0, 16) } }
        val frame2 = Random(seed = 24).let { r -> List(width * height) { r.nextInt(0, 16) } }
        val frame3 = Random(seed = 24).let { r -> List(width * height) { r.nextInt(0, 16) } }
        val frames = listOf(frame1, frame2, frame3)

        val sourceSprite = Sprite(
            width = width,
            height = height,
            palettes = listOf(sourcePalette),
            frames = frames,
        )

        val pixels = SpriteExporter.exportV0(sourceSprite)
        val resultSprite = SpriteImporter.import(pixels)

        assertEquals(sourceSprite.palettes[0].colors, resultSprite.palettes[0].colors)
        assertEquals(sourceSprite, resultSprite)
    }


    @Test
    fun checkSpriteExportImportV1() {
        val sourcePalette1 = Palette(
            colors = listOf(
                Color.Transparent,
                Color.Green,
                Color.Red,
                Color.Blue,
                Color.Cyan,
                Color.Yellow,
                Color.Magenta,
                Color.White,
                Color.Black,
                Color.LightGray,
                Color.Gray,
                Color.DarkGray,
                Color(128, 64, 234),
                Color(84, 65, 248),
                Color(20, 129, 212),
                Color(238, 178, 98),
            ).map {
                it.toArgb()
            }
        )
        val sourcePalette2 = Palette(
            colors = listOf(
                Color.Transparent,
                Color.Blue,
                Color.Cyan,
                Color.Yellow,
                Color.Green,
                Color.Red,
                Color.Black,
                Color.LightGray,
                Color.Magenta,
                Color.White,
                Color.Gray,
                Color.DarkGray,
                Color(20, 129, 212),
                Color(128, 64, 234),
                Color(238, 178, 98),
                Color(84, 65, 248),
            ).map {
                it.toArgb()
            }
        )
        val width = 13
        val height = 13

        val frame1 = Random(seed = 42).let { r -> List(width * height) { r.nextInt(0, 16) } }
        val frame2 = Random(seed = 24).let { r -> List(width * height) { r.nextInt(0, 16) } }
        val frame3 = Random(seed = 24).let { r -> List(width * height) { r.nextInt(0, 16) } }
        val frames = listOf(frame1, frame2, frame3)

        val sourceSprite = Sprite(
            width = width,
            height = height,
            palettes = listOf(sourcePalette1, sourcePalette2),
            frames = frames,
        )

        val pixels = SpriteExporter.exportV1(sourceSprite)
        val resultSprite = SpriteImporter.import(pixels)

        sourceSprite.palettes.forEachIndexed { index, palette ->
            assertEquals(palette.colors, resultSprite.palettes[index].colors)
        }
        assertEquals(sourceSprite, resultSprite)
    }
}
