package com.wechantloup.spritehandler.animCreation

internal sealed interface AnimationCreationIntent

data object PickSpriteIntent: AnimationCreationIntent
data object PickAnimationIntent: AnimationCreationIntent
data object GenerateAnimationIntent: AnimationCreationIntent
data class AddAnimationFrameIntent(val index: Int): AnimationCreationIntent
data class SetSpriteFrameIntent(val animationIndex: Int, val spriteFrameIndex: Int): AnimationCreationIntent
data class SetHorizontalOffsetIntent(val animationIndex: Int, val increment: Int): AnimationCreationIntent
data class SetVerticalOffsetIntent(val animationIndex: Int, val increment: Int): AnimationCreationIntent
