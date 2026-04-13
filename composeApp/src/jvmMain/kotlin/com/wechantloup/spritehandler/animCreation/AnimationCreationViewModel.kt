package com.wechantloup.spritehandler.animCreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.wechantloup.spritehandler.importer.SpriteImporter
import javax.swing.JFileChooser
import javax.swing.SwingUtilities.invokeAndWait
import javax.swing.filechooser.FileNameExtensionFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
            is GenerateAnimationIntent -> TODO()
            is PickSpriteIntent -> pickSprite()
            is AddAnimationFrameIntent -> TODO()
        }
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

    private fun loadSprite(file: File) {
        val bytes = file.readBytes().toList()
        val sprite = SpriteImporter.import(bytes)
        _stateFlow.value = stateFlow.value.copy(
            sprite = sprite,
        )
    }

    internal class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            @Suppress("UNCHECKED_CAST")
            return AnimationCreationViewModel() as T
        }
    }
}
