package com.wechantloup.spritehandler.animCreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wechantloup.spritehandler.composeElement.AnimationFrame
import com.wechantloup.spritehandler.composeElement.SpriteFrame
import com.wechantloup.spritehandler.composeElement.dialog.Dialog
import com.wechantloup.spritehandler.composeElement.dialog.OpenedDialogState
import com.wechantloup.spritehandler.composeElement.TopAppBar
import com.wechantloup.spritehandler.model.Animation
import com.wechantloup.spritehandler.model.Sprite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import spritehandler.composeapp.generated.resources.Res
import spritehandler.composeapp.generated.resources.animation_creation_screen_title
import spritehandler.composeapp.generated.resources.animation_horizontal_offset_label
import spritehandler.composeapp.generated.resources.animation_sprite_frame_index_label
import spritehandler.composeapp.generated.resources.animation_vertical_offset_label
import spritehandler.composeapp.generated.resources.back_btn_label
import spritehandler.composeapp.generated.resources.preview_btn_label
import spritehandler.composeapp.generated.resources.save_btn_label
import spritehandler.composeapp.generated.resources.select_animation_btn_label
import spritehandler.composeapp.generated.resources.select_sprite_btn_label

@Composable
internal fun AnimationCreationScreen(
    viewModel: AnimationCreationViewModel,
    back: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val uiState by viewModel.stateFlow.collectAsState()

    val channel = viewModel.intentChannel
    val sendIntent: (AnimationCreationIntent) -> Unit = { intent ->
        channel.sendIntent(coroutineScope, intent)
    }

    AnimationCreationScreen(
        back = back,
        state = uiState,
        sendIntent = sendIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimationCreationScreen(
    state: AnimationCreationState,
    sendIntent: (AnimationCreationIntent) -> Unit,
    back: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                titleRes = Res.string.animation_creation_screen_title,
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                navigationIconDescriptionRes = Res.string.back_btn_label,
                onNavigationPressed = back,
                actions = {
                    Button(
                        onClick = { sendIntent(GenerateAnimationIntent) }
                    ) {
                        Text(
                            text = stringResource(Res.string.save_btn_label)
                        )
                    }
                    Button(
                        onClick = { sendIntent(PreviewIntent) }
                    ) {
                        Text(
                            text = stringResource(Res.string.preview_btn_label)
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
                TopButtonsBlock(
                    sprite = state.sprite,
                    sendIntent = sendIntent,
                )

                if (state.sprite == null) return@Column

                Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    SpriteBlock(
                        sprite = state.sprite,
                        modifier = Modifier.weight(0.5f),
                    )

                    AnimationBlock(
                        animation = state.animation,
                        sprite = state.sprite,
                        sendIntent = sendIntent,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            if (state.dialog is OpenedDialogState) {
                Dialog(state.dialog)
            }
        }
    }
}

@Composable
private fun AnimationBlock(
    animation: Animation,
    sprite: Sprite,
    sendIntent: (AnimationCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val verticalPadding = 8.dp
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(verticalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        items(
            count = animation.frames.size
        ) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalPadding),
            ) {
                AddFrameButton { sendIntent(AddAnimationFrameIntent(index)) }
                AnimationFrame(
                    frame = animation.frames[index],
                    index = index,
                    sprite = sprite,
                    sendIntent = sendIntent,
                )
            }
        }
        item {
            AddFrameButton { sendIntent(AddAnimationFrameIntent(animation.frames.size)) }
        }
    }
}

@Composable
private fun AnimationFrame(
    frame: Animation.Frame,
    index: Int,
    sprite: Sprite,
    sendIntent: (AnimationCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(stringResource(Res.string.animation_sprite_frame_index_label))

            var frameIndexStr by remember(frame.spriteFrameIndex) { mutableStateOf(frame.spriteFrameIndex.toString()) }
            TextField(
                value = frameIndexStr,
                onValueChange = {
                    if (it.all { c -> c.isDigit() }) frameIndexStr = it
                    val frameIndex = frameIndexStr.toIntOrNull()?.coerceIn(0, 255)
                    frameIndex?.let { sendIntent(SetSpriteFrameIntent(index, frameIndex)) }
                }
            )
        }
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(Res.string.animation_horizontal_offset_label),
                fontSize = 12.sp,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = { sendIntent(SetHorizontalOffsetIntent(index, -1)) },
                    contentPadding = PaddingValues(2.dp),
                    modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                ) {
                    Text("-1")
                }
                Text(
                    text = frame.offsetX.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(48.dp),
                )
                Button(
                    onClick = { sendIntent(SetHorizontalOffsetIntent(index, 1)) },
                    contentPadding = PaddingValues(2.dp),
                    modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                ) {
                    Text("+1")
                }
            }
        }
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(Res.string.animation_vertical_offset_label),
                fontSize = 12.sp,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = { sendIntent(SetVerticalOffsetIntent(index, -1)) },
                    contentPadding = PaddingValues(2.dp),
                    modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                ) {
                    Text("-1")
                }
                Text(
                    text = frame.offsetX.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(48.dp),
                )
                Button(
                    onClick = { sendIntent(SetVerticalOffsetIntent(index, 1)) },
                    contentPadding = PaddingValues(2.dp),
                    modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                ) {
                    Text("+1")
                }
            }
        }
        AnimationFrame(
            frame = frame,
            sprite = sprite,
            spotSize = 2.dp,
            showHalo = false,
            diffuserBlur = 0.dp,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AddFrameButton(
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
    ) {
        Text("+")
    }
}

@Composable
private fun SpriteBlock(
    sprite: Sprite,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        items(
            count = sprite.frames.size,
        ) { index ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$index",
                    modifier = Modifier.width(32.dp),
                )
                SpriteFrame(
                    frame = sprite.frames[index],
                    palette = sprite.palette,
                    width = sprite.width,
                    height = sprite.height,
                    spotSize = 2.dp,
                    showHalo = false,
                    diffuserBlur = 0.dp,
                )
            }
        }
    }
}

@Composable
private fun TopButtonsBlock(
    sprite: Sprite?,
    sendIntent: (AnimationCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { sendIntent(PickSpriteIntent) },
        ) {
            Text(stringResource(Res.string.select_sprite_btn_label))
        }
        Button(
            enabled = sprite != null,
            onClick = { sendIntent(PickAnimationIntent) },
        ) {
            Text(stringResource(Res.string.select_animation_btn_label))
        }
    }
}

private fun Channel<AnimationCreationIntent>.sendIntent(scope: CoroutineScope, intent: AnimationCreationIntent) {
    scope.launch { send(intent) }
}
