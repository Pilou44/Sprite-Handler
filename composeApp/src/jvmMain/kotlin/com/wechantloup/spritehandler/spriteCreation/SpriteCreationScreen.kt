package com.wechantloup.spritehandler.spriteCreation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wechantloup.spritehandler.composeElement.dialog.Dialog
import com.wechantloup.spritehandler.composeElement.dialog.OpenedDialogState
import com.wechantloup.spritehandler.composeElement.TopAppBar
import com.wechantloup.spritehandler.model.Image
import com.wechantloup.spritehandler.model.Palette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import spritehandler.composeapp.generated.resources.Res
import spritehandler.composeapp.generated.resources.add_palette_btn_label
import spritehandler.composeapp.generated.resources.back_btn_label
import spritehandler.composeapp.generated.resources.generate_btn_label
import spritehandler.composeapp.generated.resources.gererate_palette_btn_label
import spritehandler.composeapp.generated.resources.load_palette_btn_label
import spritehandler.composeapp.generated.resources.select_folder_btn_label
import spritehandler.composeapp.generated.resources.sprite_creation_screen_title

@Composable
internal fun SpriteCreationScreen(
    viewModel: SpriteCreationViewModel,
    back: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val uiState by viewModel.stateFlow.collectAsState()

    val channel = viewModel.intentChannel
    val sendIntent: (SpriteCreationIntent) -> Unit = { intent ->
        channel.sendIntent(coroutineScope, intent)
    }

    SpriteCreationScreen(
        back = back,
        state = uiState,
        sendIntent = sendIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SpriteCreationScreen(
    state: SpriteCreationState,
    sendIntent: (SpriteCreationIntent) -> Unit,
    back: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                titleRes = Res.string.sprite_creation_screen_title,
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                navigationIconDescriptionRes = Res.string.back_btn_label,
                onNavigationPressed = back,
                actions = {
                    Button(
                        onClick = { sendIntent(GenerateSpriteIntent) }
                    ) {
                        Text(
                            text = stringResource(Res.string.generate_btn_label)
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
            ) {
                PickFolderBlock(sendIntent = sendIntent)

                val images = state.images
                AnimatedVisibility(images.isNotEmpty()) {
                    Column {
                        FolderDescriptionBlock(
                            name = state.folderName,
                            selectionCount = state.selectedImageCount,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        HorizontalDivider()
                        PaletteBlock(
                            palettes = state.palettes,
                            sendIntent = sendIntent,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        HorizontalDivider()
                        ImagesBlock(
                            images = images,
                            sendIntent = sendIntent,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
            if (state.dialog is OpenedDialogState) {
                Dialog(state.dialog)
            }
        }
    }
}

@Composable
private fun PickFolderBlock(
    sendIntent: (SpriteCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { sendIntent(PickFolderIntent) },
        modifier = modifier,
    ) {
        Text(stringResource(Res.string.select_folder_btn_label))
    }
}

@Composable
private fun FolderDescriptionBlock(
    name: String,
    selectionCount: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "$name - $selectionCount selected image(s)",
        modifier = modifier,
    )
}
@Composable
private fun PaletteBlock(
    palettes: List<Palette>,
    sendIntent: (SpriteCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { sendIntent(GeneratePaletteIntent) },
            ) {
                Text(stringResource(Res.string.gererate_palette_btn_label))
            }
            Button(
                onClick = { sendIntent(LoadPaletteIntent) },
            ) {
                Text(stringResource(Res.string.load_palette_btn_label))
            }
        }
        palettes.forEachIndexed { index, palette ->
            Row {
                ColorPalette(
                    palette = palette,
                    paletteIndex = index,
                    sendIntent = sendIntent,
                )
                if (index == palettes.lastIndex) {
                    Button(
                        onClick = { sendIntent(AddPaletteIntent) },
                    ) {
                        Text(stringResource(Res.string.add_palette_btn_label))
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPalette(
    palette: Palette,
    paletteIndex: Int,
    sendIntent: (SpriteCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        for (i in 0 until 15) {
            ColorPicker(
                colorArgb = palette.colors[i + 1],
                onClick = { sendIntent(ShowColorPickerIntent(colorIndex = i + 1, paletteIndex = paletteIndex)) },
            )
        }
    }
}

@Composable
private fun ColorPicker(
    colorArgb: Int,
    onClick: () -> Unit,
) {
    val color = Color(colorArgb)
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(color)
            .border(
                width = 1.dp,
                color = Color.Black,
            )
            .clickable { onClick() },
    )
}

@Composable
private fun ImagesBlock(
    images: List<Image>,
    sendIntent: (SpriteCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = modifier,
    ) {
        if (images.isNotEmpty()) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = { sendIntent(SelectAllImagesIntent) }
                    ) {
                        Text("Select all")
                    }
                    Button(
                        onClick = { sendIntent(UnselectAllImagesIntent) }
                    ) {
                        Text("Unselect all")
                    }
                }
            }
        }
        items(
            count = images.size,
        ) { index ->
            val image = images[index]
            ImageItem(
                image = image,
                selectImage = { checked -> sendIntent(SelectImageIntent(image.name, checked)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ImageItem(
    image: Image,
    selectImage: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        Checkbox(
            checked = image.isSelected,
            onCheckedChange = { checked -> selectImage(checked) },
        )
        Text(
            text = image.name,
            maxLines = 1,
            modifier = Modifier.weight(1f, fill = false),
        )
        Text("${image.width}x${image.height}")
        AsyncImage(
            model = image.path,
            contentDescription = null,
        )
    }
}

private fun Channel<SpriteCreationIntent>.sendIntent(scope: CoroutineScope, intent: SpriteCreationIntent) {
    scope.launch { send(intent) }
}
