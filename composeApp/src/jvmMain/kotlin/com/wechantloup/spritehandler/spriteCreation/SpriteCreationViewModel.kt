package com.wechantloup.spritehandler.spriteCreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.wechantloup.spritehandler.spriteCreation.model.Image
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.reflect.KClass

internal class SpriteCreationViewModel: ViewModel() {

    private val _stateFlow = MutableStateFlow(SpriteCreationState())
    val stateFlow: StateFlow<SpriteCreationState> = _stateFlow

    val intentChannel = Channel<SpriteCreationIntent>(Channel.UNLIMITED)

    init {
        viewModelScope.launch {
            intentChannel.consumeEach { handleIntent(it) }
        }
    }

    private fun handleIntent(intent: SpriteCreationIntent) {
        when (intent) {
            is PickFolderIntent -> pickFolder()
            is SelectImageIntent -> selectImage(intent.name, intent.selected)
            SelectAllImagesIntent -> selectAllImages(true)
            UnselectAllImagesIntent -> selectAllImages(false)
        }
    }

    private fun selectImage(name: String, selected: Boolean) {
        val images = stateFlow.value.images.toMutableList()
        val index = images.indexOfFirst { it.name == name }
        var image = images.removeAt(index)
        image = image.copy(isSelected = selected)
        images.add(index, image)
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
        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialogTitle = "Pick folder"
        }
        val result = chooser.showOpenDialog(null)

        if (result != JFileChooser.APPROVE_OPTION) return

        val folder = chooser.selectedFile.absoluteFile
        loadFolder(folder)
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

internal data class SpriteCreationState(
    val folderName: String = "",
    val images: List<Image> = emptyList()
) {
    val selectedImageCount: Int
        get() = images.filter { it.isSelected }.size
}

internal sealed interface SpriteCreationIntent
internal data object PickFolderIntent: SpriteCreationIntent
internal data object SelectAllImagesIntent: SpriteCreationIntent
internal data object UnselectAllImagesIntent: SpriteCreationIntent
internal data class SelectImageIntent(
    val name: String,
    val selected: Boolean,
): SpriteCreationIntent
