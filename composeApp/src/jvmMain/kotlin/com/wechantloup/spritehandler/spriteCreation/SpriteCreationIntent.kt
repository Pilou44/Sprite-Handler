package com.wechantloup.spritehandler.spriteCreation

internal sealed interface SpriteCreationIntent

internal data object PickFolderIntent: SpriteCreationIntent
internal data object SelectAllImagesIntent: SpriteCreationIntent
internal data object UnselectAllImagesIntent: SpriteCreationIntent
internal data object GenerateSpriteIntent: SpriteCreationIntent
internal data class SelectImageIntent(
    val name: String,
    val selected: Boolean,
): SpriteCreationIntent
internal data class GeneratePaletteIntent(
    val selected: Boolean,
): SpriteCreationIntent
internal data class ShowColorPickerIntent(val index: Int): SpriteCreationIntent
