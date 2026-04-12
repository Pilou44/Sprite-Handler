package com.wechantloup.spritehandler.spriteCreation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.South
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.West
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.wechantloup.spritehandler.composeElement.dialog.ClosedDialogState
import com.wechantloup.spritehandler.composeElement.dialog.OpenedDialogState
import com.wechantloup.spritehandler.model.Image
import com.wechantloup.spritehandler.model.SpriteAlignment
import com.wechantloup.spritehandler.useCase.SpriteUseCase
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.SwingUtilities.invokeAndWait
import javax.swing.filechooser.FileNameExtensionFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import spritehandler.composeapp.generated.resources.Res
import spritehandler.composeapp.generated.resources.blue_field_label
import spritehandler.composeapp.generated.resources.cancel_btn_label
import spritehandler.composeapp.generated.resources.creation_progress_checking_palette
import spritehandler.composeapp.generated.resources.creation_progress_dialog_title
import spritehandler.composeapp.generated.resources.creation_progress_done
import spritehandler.composeapp.generated.resources.creation_progress_encoding
import spritehandler.composeapp.generated.resources.creation_progress_error
import spritehandler.composeapp.generated.resources.creation_progress_generating_images
import spritehandler.composeapp.generated.resources.creation_progress_waiting
import spritehandler.composeapp.generated.resources.green_field_label
import spritehandler.composeapp.generated.resources.pick_color_dialog_title
import spritehandler.composeapp.generated.resources.red_field_label
import spritehandler.composeapp.generated.resources.save_btn_label
import spritehandler.composeapp.generated.resources.size_in_pixels_label
import spritehandler.composeapp.generated.resources.validate_btn_label
import java.io.File
import java.util.logging.Logger
import kotlin.reflect.KClass

internal class SpriteCreationViewModel: ViewModel() {

    private val _stateFlow = MutableStateFlow(SpriteCreationState())
    val stateFlow: StateFlow<SpriteCreationState> = _stateFlow

    val intentChannel = Channel<SpriteCreationIntent>(Channel.UNLIMITED)

    private val logger = Logger.getLogger("SpriteCreationViewModel")

    init {
        viewModelScope.launch {
            intentChannel.consumeEach { handleIntent(it) }
        }
    }

    private fun handleIntent(intent: SpriteCreationIntent) {
        when (intent) {
            is PickFolderIntent -> pickFolder()
            is SelectImageIntent -> selectImage(intent.name, intent.selected)
            is SelectAllImagesIntent -> selectAllImages(true)
            is UnselectAllImagesIntent -> selectAllImages(false)
            is GeneratePaletteIntent -> setGeneratedPalette(intent.selected)
            is ShowColorPickerIntent -> showColorPicker(intent.index)
            is GenerateSpriteIntent -> showGenerationDialog()
        }
    }

    private fun showGenerationDialog() {
        val width = mutableIntStateOf(0)
        val height = mutableIntStateOf(0)
        val alignment = mutableStateOf(SpriteAlignment.CENTER)
        val dialog = OpenedDialogState(
            onDismiss = ::closeDialog,
            titleRes = Res.string.pick_color_dialog_title,
            confirmButtonTextRes = Res.string.validate_btn_label,
            cancelButtonTextRes = Res.string.cancel_btn_label,
            body = @Composable {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SpriteSizeBlock(width, height)
                    SpriteAlignmentBlock(alignment)
                }
            },
            onConfirmButtonClicked = {
                closeDialog()
                launchSpriteGeneration(
                    width = width.value,
                    height = height.value,
                    alignment = alignment.value
                )
            },
            canConfirm = { width.value > 0 && height.value > 0 }
        )
        _stateFlow.value = stateFlow.value.copy(dialog = dialog)
    }

    @Composable
    private fun SpriteSizeBlock(
        width: MutableIntState,
        height: MutableIntState,
        modifier: Modifier = Modifier,
    ) {
        var widthStr by remember { mutableStateOf(width.value.toString()) }
        var heightStr by remember { mutableStateOf(height.value.toString()) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier,
        ) {
            Text(stringResource(Res.string.size_in_pixels_label))
            TextField(
                value = widthStr,
                onValueChange = {
                    if (it.all { c -> c.isDigit() }) widthStr = it
                    width.value = it.toIntOrNull() ?: 0
                },
                modifier = Modifier.weight(1f),
            )
            Text("x")
            TextField(
                value = heightStr,
                onValueChange = {
                    if (it.all { c -> c.isDigit() }) heightStr = it
                    height.value = it.toIntOrNull() ?: 0
                },
                modifier = Modifier.weight(1f),
            )
        }
    }

    @Composable
    private fun SpriteAlignmentBlock(
        alignment: MutableState<SpriteAlignment>,
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier,
        ) {
            Row {
                Button(
                    onClick = { alignment.value = SpriteAlignment.TOP_LEFT },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alignment.value == SpriteAlignment.TOP_LEFT) {
                            Color.Blue
                        } else {
                            Color.Unspecified
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.NorthWest,
                        contentDescription = null,
                    )
                }
                Button(
                    onClick = { alignment.value = SpriteAlignment.TOP_CENTER },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alignment.value == SpriteAlignment.TOP_CENTER) {
                            Color.Blue
                        } else {
                            Color.Unspecified
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.North,
                        contentDescription = null,
                    )
                }
                Button(
                    onClick = { alignment.value = SpriteAlignment.TOP_RIGHT },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alignment.value == SpriteAlignment.TOP_RIGHT) {
                            Color.Blue
                        } else {
                            Color.Unspecified
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.NorthEast,
                        contentDescription = null,
                    )
                }
            }
            Row {
                Button(
                    onClick = { alignment.value = SpriteAlignment.CENTER_LEFT },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alignment.value == SpriteAlignment.CENTER_LEFT) {
                            Color.Blue
                        } else {
                            Color.Unspecified
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.West,
                        contentDescription = null,
                    )
                }
                Button(
                    onClick = { alignment.value = SpriteAlignment.CENTER },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alignment.value == SpriteAlignment.CENTER) {
                            Color.Blue
                        } else {
                            Color.Unspecified
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                    )
                }
                Button(
                    onClick = { alignment.value = SpriteAlignment.CENTER_RIGHT },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alignment.value == SpriteAlignment.CENTER_RIGHT) {
                            Color.Blue
                        } else {
                            Color.Unspecified
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.East,
                        contentDescription = null,
                    )
                }
            }
            Row {
                Button(
                    onClick = { alignment.value = SpriteAlignment.BOTTOM_LEFT },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alignment.value == SpriteAlignment.BOTTOM_LEFT) {
                            Color.Blue
                        } else {
                            Color.Unspecified
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SouthWest,
                        contentDescription = null,
                    )
                }
                Button(
                    onClick = { alignment.value = SpriteAlignment.BOTTOM_CENTER },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alignment.value == SpriteAlignment.BOTTOM_CENTER) {
                            Color.Blue
                        } else {
                            Color.Unspecified
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.South,
                        contentDescription = null,
                    )
                }
                Button(
                    onClick = { alignment.value = SpriteAlignment.BOTTOM_RIGHT },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alignment.value == SpriteAlignment.BOTTOM_RIGHT) {
                            Color.Blue
                        } else {
                            Color.Unspecified
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SouthEast,
                        contentDescription = null,
                    )
                }
            }
        }
    }

    private fun launchSpriteGeneration(
        width: Int,
        height: Int,
        alignment: SpriteAlignment,
    ) {
        logger.severe("launchSpriteGeneration - start")
        val state = mutableStateOf(GenerationState.IDLE)
        val bytes = mutableStateOf<List<Byte>>(emptyList())

        val job = viewModelScope.launch(Dispatchers.IO) {
            val colorList = stateFlow.value.palette
            val images = stateFlow.value.images.filter { it.isSelected }
            val autoGeneratedPalette = stateFlow.value.autoGeneratedPalette

            logger.severe("Check palette")
            state.value = GenerationState.CHECKING_PALETTE
            val palette = try {
                SpriteUseCase.getPalette(
                    autoGenerated = autoGeneratedPalette,
                    images = images,
                    selectedColors = colorList,
                )
            } catch (e: Exception) {
                logger.severe("Error generating palette: ${e.printStackTrace()}")
                state.value = GenerationState.ERROR
                return@launch
            }

            logger.severe("Generate images")
            state.value = GenerationState.GENERATING_IMAGES
            val pixelColors = try {
                SpriteUseCase.generateSprite(
                    images = images,
                    palette = palette,
                    width = width,
                    height = height,
                    alignment = alignment,
                )
            } catch (e: Exception) {
                logger.severe("Error generating images: ${e.printStackTrace()}")
                state.value = GenerationState.ERROR
                return@launch
            }

            logger.severe("Encode")
            state.value = GenerationState.ENCODING
            bytes.value = try {
                SpriteUseCase.encode(
                    width = width,
                    height = height,
                    palette = palette,
                    imageCount = images.size,
                    pixelColors = pixelColors,
                )
            } catch (e: Exception) {
                logger.severe("Error encoding information: ${e.printStackTrace()}")
                state.value = GenerationState.ERROR
                return@launch
            }

            logger.severe("launchSpriteGeneration - done")
            state.value = GenerationState.DONE
        }

        val dialog = OpenedDialogState(
            canDismiss = false,
            onDismiss = ::closeDialog,
            titleRes = Res.string.creation_progress_dialog_title,
            body = {
                val msgId = when(state.value) {
                    GenerationState.IDLE -> Res.string.creation_progress_waiting
                    GenerationState.CHECKING_PALETTE -> Res.string.creation_progress_checking_palette
                    GenerationState.GENERATING_IMAGES -> Res.string.creation_progress_generating_images
                    GenerationState.ENCODING -> Res.string.creation_progress_encoding
                    GenerationState.DONE -> Res.string.creation_progress_done
                    GenerationState.ERROR -> Res.string.creation_progress_error
                }
                Text(text = stringResource(msgId))
            },
            confirmButtonTextRes = Res.string.save_btn_label,
            cancelButtonTextRes = Res.string.cancel_btn_label,
            onConfirmButtonClicked = {
                saveFile(bytes.value)
            },
            onCancelButtonClicked = {
                job.cancel()
                closeDialog()
            },
            canConfirm = { state.value == GenerationState.DONE },
        )
        _stateFlow.value = stateFlow.value.copy(dialog = dialog)
    }

    private fun saveFile(bytes: List<Byte>) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = saveFileDialog() ?: return@launch
            file.writeBytes(bytes.toByteArray())

            closeDialog()
        }
    }

    private fun saveFileDialog(): File? {
        var file: File? = null
        invokeAndWait {
            val chooser = JFileChooser().apply {
                dialogTitle = "Sauvegarder le fichier"
                fileSelectionMode = JFileChooser.FILES_ONLY
                fileFilter = FileNameExtensionFilter("Sprite files (*.spr)", "spr")
            }

            val result = chooser.showSaveDialog(null)

            if (result == JFileChooser.APPROVE_OPTION) {
                val selected = chooser.selectedFile
                file = if (selected.extension.lowercase() == "spr") {
                    selected
                } else {
                    File(selected.absolutePath + ".spr")
                }
            }
        }
        return file
    }

    private fun showColorPicker(index: Int) {
        val color = Color(stateFlow.value.palette[index])
        var red by mutableStateOf((color.red * 255).toInt().toString())
        var green by mutableStateOf((color.green * 255).toInt().toString())
        var blue by mutableStateOf((color.blue * 255).toInt().toString())
        val dialog = OpenedDialogState(
            onDismiss = ::closeDialog,
            titleRes = Res.string.pick_color_dialog_title,
            confirmButtonTextRes = Res.string.validate_btn_label,
            cancelButtonTextRes = Res.string.cancel_btn_label,
            body = @Composable {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Row {
                        Text(stringResource(Res.string.red_field_label))
                        TextField(
                            value = red,
                            onValueChange = { if (it.all { c -> c.isDigit() }) red = it },
                        )
                    }
                    Row {
                        Text(stringResource(Res.string.green_field_label))
                        TextField(
                            value = green,
                            onValueChange = { if (it.all { c -> c.isDigit() }) green = it },
                        )
                    }
                    Row {
                        Text(stringResource(Res.string.blue_field_label))
                        TextField(
                            value = blue,
                            onValueChange = { if (it.all { c -> c.isDigit() }) blue = it },
                        )
                    }
                    Box(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp)
                            .size(64.dp)
                            .background(
                                Color(
                                    red = red.toIntOrNull()?.coerceIn(0, 255) ?: 0,
                                    green = green.toIntOrNull()?.coerceIn(0, 255) ?: 0,
                                    blue = blue.toIntOrNull()?.coerceIn(0, 255) ?: 0,
                                    alpha = 255,
                                )
                            ),
                    )
                }
            },
            onConfirmButtonClicked = {
                val newColor = Color(
                    red = red.toIntOrNull()?.coerceIn(0, 255) ?: 0,
                    green = green.toIntOrNull()?.coerceIn(0, 255) ?: 0,
                    blue = blue.toIntOrNull()?.coerceIn(0, 255) ?: 0,
                    alpha = 255,
                )
                val palette = _stateFlow.value.palette.toMutableList()
                palette[index] = newColor.toArgb()
                _stateFlow.value = stateFlow.value.copy(
                    palette = palette,
                    dialog = ClosedDialogState,
                )
            },
        )
        _stateFlow.value = stateFlow.value.copy(dialog = dialog)
    }

    private fun closeDialog() {
        _stateFlow.value = stateFlow.value.copy(dialog = ClosedDialogState)
    }

    private fun setGeneratedPalette(generated: Boolean) {
        _stateFlow.value = stateFlow.value.copy(autoGeneratedPalette = generated)
    }

    private fun selectImage(name: String, selected: Boolean) {
        val images = stateFlow.value.images.toMutableList()
        val index = images.indexOfFirst { it.name == name }
        images[index] = images[index].copy(isSelected = selected)
        _stateFlow.value = stateFlow.value.copy(
            images = images,
        )
    }

    private fun selectAllImages(selected: Boolean) {
        val images = stateFlow
            .value
            .images
            .map { it.copy(isSelected = selected) }
        _stateFlow.value = stateFlow.value.copy(
            images = images,
        )
    }

    private fun pickFolder() {
        viewModelScope.launch(Dispatchers.IO) {
            var folder: File? = null
            invokeAndWait {
                val chooser = JFileChooser().apply {
                    fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    dialogTitle = "Pick folder"
                }
                val result = chooser.showOpenDialog(null)

                if (result == JFileChooser.APPROVE_OPTION) {
                    folder = chooser.selectedFile.absoluteFile
                }
            }
            folder?.let { loadFolder(folder) }
        }
    }

    private fun loadFolder(folder: File) {
        val pngFiles = folder
            .listFiles { file ->
                file.isFile && file.isPng()
            }
            .sortedBy { it.name }
        val images = pngFiles.mapNotNull {
            val size = it.getImageSize() ?: return@mapNotNull null
            Image(
                name = it.name,
                path = it.path,
                width = size.first,
                height = size.second,
                isSelected = false,
            )
        }

        _stateFlow.value = stateFlow.value.copy(
            folderName = folder.name,
            images = images,
        )
    }

    private fun File.isPng(): Boolean {
        val header = ByteArray(8)
        inputStream().use { it.read(header) }
        return header.contentEquals(byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        ))
    }

    private fun File.getImageSize(): Pair<Int, Int>? {
        val image = ImageIO.read(this) ?: return null
        return image.width to image.height
    }

    internal class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            @Suppress("UNCHECKED_CAST")
            return SpriteCreationViewModel() as T
        }
    }
}
