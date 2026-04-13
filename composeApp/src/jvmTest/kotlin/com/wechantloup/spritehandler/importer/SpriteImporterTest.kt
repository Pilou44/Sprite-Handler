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
    fun checkSpriteExportImport() {
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
            palette = sourcePalette,
            frames = frames,
        )

        val pixels = SpriteExporter.export(sourceSprite)
        val resultSprite = SpriteImporter.import(pixels)

        assertEquals(sourceSprite.palette, resultSprite.palette)
        assertEquals(sourceSprite, resultSprite)
    }

}
