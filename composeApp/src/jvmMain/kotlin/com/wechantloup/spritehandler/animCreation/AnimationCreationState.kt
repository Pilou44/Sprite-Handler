package com.wechantloup.spritehandler.animCreation

import com.wechantloup.spritehandler.model.Animation
import com.wechantloup.spritehandler.model.Sprite

data class AnimationCreationState(
    val sprite: Sprite? = null,
    val animation: Animation = Animation(emptyList()),
)

