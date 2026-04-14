package com.wechantloup.spritehandler.animCreation

import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.wechantloup.spritehandler.composeElement.dialog.OpenedDialogState
import com.wechantloup.spritehandler.exporter.AnimationExporter
import com.wechantloup.spritehandler.exporter.SpriteExporter
import com.wechantloup.spritehandler.importer.AnimationImporter
import com.wechantloup.spritehandler.importer.SpriteImporter
import com.wechantloup.spritehandler.model.Animation.Frame
import com.wechantloup.spritehandler.model.Sprite
import com.wechantloup.spritehandler.model.SpriteAlignment
import com.wechantloup.spritehandler.spriteCreation.GenerationState
import com.wechantloup.spritehandler.useCase.SpriteUseCase
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
import spritehandler.composeapp.generated.resources.cancel_btn_label
import spritehandler.composeapp.generated.resources.creation_progress_checking_palette
import spritehandler.composeapp.generated.resources.creation_progress_dialog_title
import spritehandler.composeapp.generated.resources.creation_progress_done
import spritehandler.composeapp.generated.resources.creation_progress_encoding
import spritehandler.composeapp.generated.resources.creation_progress_error
import spritehandler.composeapp.generated.resources.creation_progress_generating_images
import spritehandler.composeapp.generated.resources.creation_progress_waiting
import spritehandler.composeapp.generated.resources.save_btn_label
import java.io.File
import java.util.logging.Logger
import kotlin.reflect.KClass

internal class AnimationCreationViewModel: ViewModel() {

    private val _stateFlow = MutableStateFlow(AnimationCreationState())
    val stateFlow: StateFlow<AnimationCreationState> = _stateFlow

    val intentChannel = Channel<AnimationCreationIntent>(Channel.UNLIMITED)

    private val logger = Logger.getLogger("AnimationCreationViewModel")

    init {
        viewModelScope.launch {
            intentChannel.consumeEach { handleIntent(it) }
        }
    }

    private fun handleIntent(intent: AnimationCreationIntent) {
        when (intent) {
            is GenerateAnimationIntent -> launchAnimationGeneration()
            is PickSpriteIntent -> pickSprite()
            is PickAnimationIntent -> pickAnimation()
            is AddAnimationFrameIntent -> addAnimationFrame(intent.index)
            is SetHorizontalOffsetIntent -> setFramePadding(intent.animationIndex, incX = intent.increment)
            is SetVerticalOffsetIntent -> setFramePadding(intent.animationIndex, incY = intent.increment)
            is SetSpriteFrameIntent -> setSpriteFrame(intent.animationIndex, intent.spriteFrameIndex)
        }
    }

    private fun launchAnimationGeneration() {
        // ToDo show loader
        viewModelScope.launch(Dispatchers.IO) {
            val animation = stateFlow.value.animation
            val animationBytes = AnimationExporter.export(animation)


            val file = saveFileDialog() ?: return@launch
            file.writeBytes(animationBytes.toByteArray())
        }
    }

    private fun saveFileDialog(): File? {
        var file: File? = null
        invokeAndWait {
            val chooser = JFileChooser().apply {
                dialogTitle = "Sauvegarder le fichier"
                fileSelectionMode = JFileChooser.FILES_ONLY
                fileFilter = FileNameExtensionFilter("Animation files (*.anim)", "anim")
            }

            val result = chooser.showSaveDialog(null)

            if (result == JFileChooser.APPROVE_OPTION) {
                val selected = chooser.selectedFile
                file = if (selected.extension.lowercase() == "anim") {
                    selected
                } else {
                    File(selected.absolutePath + ".anim")
                }
            }
        }
        return file
    }

    private fun addAnimationFrame(index: Int) {
        val animation = stateFlow.value.animation
        val frames = animation.frames.toMutableList()
        frames.add(index, Frame(0, 0, 0))
        val newAnimation = animation.copy(frames = frames)
        _stateFlow.value = stateFlow.value.copy(
            animation = newAnimation
        )
    }

    private fun setFramePadding(index: Int, incX: Int = 0, incY: Int = 0) {
        val animation = stateFlow.value.animation
        val frames = animation.frames.toMutableList()
        val frame = frames.removeAt(index)
        val newFrame = frame.copy(
            offsetX = frame.offsetX + incX,
            offsetY = frame.offsetY + incY,
        )
        frames.add(index, newFrame)
        val newAnimation = animation.copy(frames = frames)
        _stateFlow.value = stateFlow.value.copy(
            animation = newAnimation
        )
    }

    private fun setSpriteFrame(index: Int, spriteIndex: Int) {
        val animation = stateFlow.value.animation
        val frames = animation.frames.toMutableList()
        val frame = frames.removeAt(index)
        val newFrame = frame.copy(
            spriteFrameIndex = spriteIndex,
        )
        frames.add(index, newFrame)
        val newAnimation = animation.copy(frames = frames)
        _stateFlow.value = stateFlow.value.copy(
            animation = newAnimation
        )
    }

    private fun pickSprite() {
        viewModelScope.launch(Dispatchers.IO) {
            var file: File? = null
            invokeAndWait {
                val chooser = JFileChooser().apply {
                    fileSelectionMode = JFileChooser.FILES_ONLY
                    dialogTitle = "Pick sprite"
                    fileFilter = FileNameExtensionFilter("Sprite files (*.spr)", "spr")
                }
                val result = chooser.showOpenDialog(null)

                if (result == JFileChooser.APPROVE_OPTION) {
                    file = chooser.selectedFile.absoluteFile
                }
            }
            file?.let { loadSprite(file) }
        }
    }

    private fun pickAnimation() {
        viewModelScope.launch(Dispatchers.IO) {
            var file: File? = null
            invokeAndWait {
                val chooser = JFileChooser().apply {
                    fileSelectionMode = JFileChooser.FILES_ONLY
                    dialogTitle = "Pick animation"
                    fileFilter = FileNameExtensionFilter("Animation files (*.anim)", "anim")
                }
                val result = chooser.showOpenDialog(null)

                if (result == JFileChooser.APPROVE_OPTION) {
                    file = chooser.selectedFile.absoluteFile
                }
            }
            file?.let { loadAnimation(file) }
        }
    }

    private fun loadSprite(file: File) {
        val bytes = file.readBytes().toList()
        val sprite = SpriteImporter.import(bytes)
        _stateFlow.value = stateFlow.value.copy(
            sprite = sprite,
        )
    }

    private fun loadAnimation(file: File) {
        val bytes = file.readBytes().toList()
        val animation = AnimationImporter.import(bytes)
        _stateFlow.value = stateFlow.value.copy(
            animation = animation,
        )
    }

    internal class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            @Suppress("UNCHECKED_CAST")
            return AnimationCreationViewModel() as T
        }
    }
}
