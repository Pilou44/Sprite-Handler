package com.wechantloup.spritehandler.importer

import com.wechantloup.spritehandler.exporter.AnimationExporter
import com.wechantloup.spritehandler.model.Animation
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimationImporterTest {
    @Test
    fun checkAnimationExportImport() {
        val frame1 = Random(seed = 42).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
            )
        }
        val frame2 = Random(seed = 27).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
            )
        }
        val frame3 = Random(seed = 13).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
            )
        }
        val sourceFrames = listOf(frame1, frame2, frame3)
        val sourceAnimation = Animation(sourceFrames, 48, 48)

        val bytes = AnimationExporter.export(sourceAnimation)
        val resultAnimation = AnimationImporter.import(bytes)

        assertEquals(sourceAnimation.frames, resultAnimation.frames)
        assertEquals(sourceAnimation, resultAnimation)
    }
}
