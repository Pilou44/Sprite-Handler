package com.wechantloup.spritehandler.animCreation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import com.wechantloup.spritehandler.spriteCreation.PickFolderIntent
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
                title = { Text(stringResource(Res.string.animation_creation_screen_title)) },
                navigationIcon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { back() },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back_btn_label),
                        )
                    }
                },
                actions = {
                    Row {
                        Button(
                            onClick = { sendIntent(GenerateAnimationIntent) }
                        ) {
                            Text(
                                text = stringResource(Res.string.generate_btn_label)
                            )
                        }
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
