package com.wechantloup.spritehandler.animCreation

internal sealed interface AnimationCreationIntent

data object PickSpriteIntent: AnimationCreationIntent
data object PickAnimationIntent: AnimationCreationIntent
data object GenerateAnimationIntent: AnimationCreationIntent
data object PreviewIntent: AnimationCreationIntent
data class AddAnimationFrameIntent(val index: Int): AnimationCreationIntent
data class SetSpriteFrameIntent(val animationIndex: Int, val spriteFrameIndex: Int): AnimationCreationIntent
data class SetHorizontalOffsetIntent(val animationIndex: Int, val increment: Int): AnimationCreationIntent
data class SetVerticalOffsetIntent(val animationIndex: Int, val increment: Int): AnimationCreationIntent
data class SetAnimationSizeIntent(val width: Int? = null, val height: Int? = null): AnimationCreationIntent
data class SetPaletteIntent(val frameIndex: Int, val paletteIndex: Int): AnimationCreationIntent
data class SetDurationIntent(val frameIndex: Int, val durationMs: Int): AnimationCreationIntent
data class SetBrightnessIntent(val frameIndex: Int, val brightness: Float): AnimationCreationIntent
data class SetHorizontallyMirroredIntent(val frameIndex: Int, val value: Boolean): AnimationCreationIntent
data class SetVerticallyMirroredIntent(val frameIndex: Int, val value: Boolean): AnimationCreationIntent
