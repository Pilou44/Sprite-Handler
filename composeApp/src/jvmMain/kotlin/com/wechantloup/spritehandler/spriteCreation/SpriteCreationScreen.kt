package com.wechantloup.spritehandler.spriteCreation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wechantloup.spritehandler.spriteCreation.model.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@Composable
internal fun SpriteCreationScreen(
    back: () -> Unit,
    viewModel: SpriteCreationViewModel,
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
    back: () -> Unit,
    state: SpriteCreationState,
    sendIntent: (SpriteCreationIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sprite creation") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Retour",
                        modifier = Modifier.clickable { back() }
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    )
                    PaletteBlock()
                    ImagesBlock(
                        images = images,
                        sendIntent = sendIntent,
                    )
                }
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
        Text("Sélectionner un dossier")
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
private fun PaletteBlock() {
    // Todo
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

