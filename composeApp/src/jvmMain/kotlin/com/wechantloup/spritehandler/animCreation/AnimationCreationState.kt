package com.wechantloup.spritehandler.animCreation

import com.wechantloup.spritehandler.composeElement.dialog.ClosedDialogState
import com.wechantloup.spritehandler.composeElement.dialog.DialogState
import com.wechantloup.spritehandler.model.Animation
import com.wechantloup.spritehandler.model.Sprite

data class AnimationCreationState(
    val sprite: Sprite? = null,
    val animation: Animation = Animation(emptyList(), 0, 0),
    val dialog: DialogState = ClosedDialogState,
)

