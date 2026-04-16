package com.wechantloup.spritehandler.animCreation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.wechantloup.spritehandler.composeElement.AnimationFrame
import com.wechantloup.spritehandler.composeElement.dialog.ClosedDialogState
import com.wechantloup.spritehandler.composeElement.dialog.OpenedDialogState
import com.wechantloup.spritehandler.exporter.AnimationExporter
import com.wechantloup.spritehandler.importer.AnimationImporter
import com.wechantloup.spritehandler.importer.SpriteImporter
import com.wechantloup.spritehandler.model.Animation
import com.wechantloup.spritehandler.model.Animation.Frame
import com.wechantloup.spritehandler.model.Sprite
import javax.swing.JFileChooser
import javax.swing.SwingUtilities.invokeAndWait
import javax.swing.filechooser.FileNameExtensionFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import spritehandler.composeapp.generated.resources.Res
import spritehandler.composeapp.generated.resources.ok_btn_label
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
            is PreviewIntent -> showPreview()
            is SetAnimationSizeIntent -> setAnimationSize(intent.width, intent.height)
            is SetFramePalette -> setFramePalette(intent.frameIndex, intent.paletteIndex)
        }
    }

    private fun setFramePalette(
        frameIndex: Int,
        paletteIndex: Int,
    ) {
        val animation = stateFlow.value.animation
        val frames = animation.frames.toMutableList()
        val frame = frames.removeAt(frameIndex)
        val newFrame = frame.copy(paletteIndex = paletteIndex)
        frames.add(frameIndex, newFrame)
        val newAnimation = animation.copy(frames = frames)
        _stateFlow.value = stateFlow.value.copy(
            animation = newAnimation,
        )
    }

    private fun showPreview() {
        val sprite = stateFlow.value.sprite ?: return
        val animation = stateFlow.value.animation

        val dialog = OpenedDialogState(
            onDismiss = ::closeDialog,
            cancelButtonTextRes = Res.string.ok_btn_label,
            body = { AnimationPreview(sprite, animation) },
        )
        _stateFlow.value = stateFlow.value.copy(dialog = dialog)
    }

    private fun closeDialog() {
        _stateFlow.value = stateFlow.value.copy(dialog = ClosedDialogState)
    }

    @Composable
    private fun AnimationPreview(
        sprite: Sprite,
        animation: Animation,
    ) {
        var frameIndex by remember { mutableIntStateOf(0) }
        LaunchedEffect(frameIndex) {
            delay(100)
            frameIndex = (frameIndex + 1) % animation.frames.size
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            AnimationFrame(
                frame = animation.frames[frameIndex],
                sprite = sprite,
                animWidth = animation.width,
                animHeight = animation.height,
                spotSize = 4.dp,
            )
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
        frames.add(index, Frame(0, 0, 0, 0))
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

    private fun setAnimationSize(width: Int?, height: Int?) {
        val animation = stateFlow.value.animation
        val newWidth = width ?: animation.width
        val newHeight = height ?: animation.height
        val newAnimation = animation.copy(width = newWidth, height = newHeight)
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
