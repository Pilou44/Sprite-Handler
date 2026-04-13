package com.wechantloup.spritehandler.animCreation

internal sealed interface AnimationCreationIntent

data object PickSpriteIntent: AnimationCreationIntent
data object GenerateAnimationIntent: AnimationCreationIntent
data class AddAnimationFrameIntent(val index: Int): AnimationCreationIntent
