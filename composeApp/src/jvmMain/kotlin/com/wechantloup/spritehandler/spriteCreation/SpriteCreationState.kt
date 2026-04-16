package com.wechantloup.spritehandler.spriteCreation

import com.wechantloup.spritehandler.composeElement.dialog.ClosedDialogState
import com.wechantloup.spritehandler.composeElement.dialog.DialogState
import com.wechantloup.spritehandler.model.Image
import com.wechantloup.spritehandler.model.Palette

internal data class SpriteCreationState(
    val folderName: String = "",
    val images: List<Image> = emptyList(),
    val palette: Palette = Palette(),
    val dialog: DialogState = ClosedDialogState,
) {
    val selectedImageCount: Int
        get() = images.filter { it.isSelected }.size
}
