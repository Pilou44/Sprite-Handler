package com.wechantloup.spritehandler.importer

import com.wechantloup.spritehandler.exporter.AnimationExporter
import com.wechantloup.spritehandler.model.Animation
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimationImporterTest {
    @Test
    fun checkAnimationExportImportV0() {
        val frame1 = Random(seed = 42).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
                paletteIndex = 0,
                durationMs = 100,
                brightness = 1f,
                isHorizontallyMirrored = false,
                isVerticallyMirrored = false,
            )
        }
        val frame2 = Random(seed = 27).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
                paletteIndex = 0,
                durationMs = 100,
                brightness = 1f,
                isHorizontallyMirrored = false,
                isVerticallyMirrored = false,
            )
        }
        val frame3 = Random(seed = 13).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
                paletteIndex = 0,
                durationMs = 100,
                brightness = 1f,
                isHorizontallyMirrored = false,
                isVerticallyMirrored = false,
            )
        }
        val sourceFrames = listOf(frame1, frame2, frame3)
        val sourceAnimation = Animation(sourceFrames, 48, 48)

        val bytes = AnimationExporter.exportV0(sourceAnimation)
        val resultAnimation = AnimationImporter.import(bytes)

        assertEquals(sourceAnimation.frames, resultAnimation.frames)
        assertEquals(sourceAnimation, resultAnimation)
    }

    @Test
    fun checkAnimationExportImportV1() {
        val frame1 = Random(seed = 42).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
                paletteIndex = 0,
                durationMs = 100,
                brightness = 1f,
                isHorizontallyMirrored = false,
                isVerticallyMirrored = false,
            )
        }
        val frame2 = Random(seed = 27).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
                paletteIndex = 1,
                durationMs = 100,
                brightness = 1f,
                isHorizontallyMirrored = false,
                isVerticallyMirrored = false,
            )
        }
        val frame3 = Random(seed = 13).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
                paletteIndex = 0,
                durationMs = 100,
                brightness = 1f,
                isHorizontallyMirrored = false,
                isVerticallyMirrored = false,
            )
        }
        val sourceFrames = listOf(frame1, frame2, frame3)
        val sourceAnimation = Animation(sourceFrames, 48, 48)

        val bytes = AnimationExporter.exportV1(sourceAnimation)
        val resultAnimation = AnimationImporter.import(bytes)

        assertEquals(sourceAnimation.frames, resultAnimation.frames)
        assertEquals(sourceAnimation, resultAnimation)
    }

    @Test
    fun checkAnimationExportImportV2() {
        val frame1 = Random(seed = 42).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
                paletteIndex = 0,
                durationMs = 100,
                brightness = 1f,
                isHorizontallyMirrored = true,
                isVerticallyMirrored = true,
            )
        }
        val frame2 = Random(seed = 27).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
                paletteIndex = 1,
                durationMs = 50,
                brightness = 0.75f,
                isHorizontallyMirrored = false,
                isVerticallyMirrored = false,
            )
        }
        val frame3 = Random(seed = 13).let { r ->
            Animation.Frame(
                spriteFrameIndex = r.nextInt(0, 255),
                offsetX = r.nextInt(-255, 255),
                offsetY = r.nextInt(-255, 255),
                paletteIndex = 0,
                durationMs = 200,
                brightness = 0.5f,
                isHorizontallyMirrored = true,
                isVerticallyMirrored = false,
            )
        }
        val sourceFrames = listOf(frame1, frame2, frame3)
        val sourceAnimation = Animation(sourceFrames, 48, 48)

        val bytes = AnimationExporter.exportV2(sourceAnimation)
        val resultAnimation = AnimationImporter.import(bytes)

        assertEquals(sourceAnimation.frames, resultAnimation.frames)
        assertEquals(sourceAnimation, resultAnimation)
    }
}
