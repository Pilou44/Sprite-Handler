package com.wechantloup.spritehandler.animCreation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wechantloup.spritehandler.composeElement.dialog.TopAppBar
import com.wechantloup.spritehandler.model.Palette
import com.wechantloup.spritehandler.model.Sprite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import spritehandler.composeapp.generated.resources.Res
import spritehandler.composeapp.generated.resources.animation_creation_screen_title
import spritehandler.composeapp.generated.resources.back_btn_label
import spritehandler.composeapp.generated.resources.generate_btn_label
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
                PickSpriteBlock(sendIntent = sendIntent)

                Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    SpriteBlock(
                        state.sprite,
                        modifier = Modifier.weight(1f),
                    )

                    AnimationBlock(
                        state.animation,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimationBlock(
    animation: Unit?,
    modifier: Modifier = Modifier,
) {

}

@Composable
private fun SpriteBlock(
    sprite: Sprite?,
    modifier: Modifier = Modifier,
) {
    if (sprite == null) return

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                )
            }
        }
    }
}

@Composable
private fun SpriteFrame(
    frame: List<Int>,
    palette: Palette,
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    spotSize: Dp = 16.dp,
) {
    Column(
        modifier = modifier.background(Color.Black)
    ) {
        for (j in 0 until height) {
            Row {
                for (i in 0 until width) {
                    val index = width * j + i
                    val colorIndex = frame[index]
                    val colorArgb = palette.colors[colorIndex]
                    val color = Color(colorArgb)
                    Box(
                        modifier = Modifier
                            .padding(spotSize / 2)
                            .size(spotSize)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
private fun PickSpriteBlock(
    sendIntent: (AnimationCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { sendIntent(PickSpriteIntent) },
        modifier = modifier,
    ) {
        Text(stringResource(Res.string.select_sprite_btn_label))
    }
}

private fun Channel<AnimationCreationIntent>.sendIntent(scope: CoroutineScope, intent: AnimationCreationIntent) {
    scope.launch { send(intent) }
}
