package com.wechantloup.spritehandler.animCreation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wechantloup.spritehandler.composeElement.AnimationFrame
import com.wechantloup.spritehandler.composeElement.SpriteFrame
import com.wechantloup.spritehandler.composeElement.TopAppBar
import com.wechantloup.spritehandler.composeElement.dialog.Dialog
import com.wechantloup.spritehandler.composeElement.dialog.OpenedDialogState
import com.wechantloup.spritehandler.model.Animation
import com.wechantloup.spritehandler.model.Palette
import com.wechantloup.spritehandler.model.Sprite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import spritehandler.composeapp.generated.resources.Res
import spritehandler.composeapp.generated.resources.animation_brightness_label
import spritehandler.composeapp.generated.resources.animation_creation_screen_title
import spritehandler.composeapp.generated.resources.animation_duration_label
import spritehandler.composeapp.generated.resources.animation_duration_min_error
import spritehandler.composeapp.generated.resources.animation_frame_index_label
import spritehandler.composeapp.generated.resources.animation_horizontal_offset_label
import spritehandler.composeapp.generated.resources.animation_palette_label
import spritehandler.composeapp.generated.resources.animation_sprite_frame_index_label
import spritehandler.composeapp.generated.resources.animation_vertical_offset_label
import spritehandler.composeapp.generated.resources.back_btn_label
import spritehandler.composeapp.generated.resources.height_field_label
import spritehandler.composeapp.generated.resources.preview_btn_label
import spritehandler.composeapp.generated.resources.save_btn_label
import spritehandler.composeapp.generated.resources.select_animation_btn_label
import spritehandler.composeapp.generated.resources.select_sprite_btn_label
import spritehandler.composeapp.generated.resources.width_field_label

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
                        enabled = state.animation.isValid,
                        onClick = { sendIntent(GenerateAnimationIntent) }
                    ) {
                        Text(
                            text = stringResource(Res.string.save_btn_label)
                        )
                    }
                    Button(
                        enabled = state.animation.isValid,
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
                    animation = state.animation,
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
                AnimationFrameEditor(
                    frame = animation.frames[index],
                    index = index,
                    animWidth = animation.width,
                    animHeight = animation.height,
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
private fun AnimationFrameEditor(
    frame: Animation.Frame,
    index: Int,
    animWidth: Int,
    animHeight: Int,
    sprite: Sprite,
    sendIntent: (AnimationCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        FrameHeader(
            index = index,
            durationMs = frame.durationMs,
            onDurationChange = { sendIntent(SetDurationIntent(index, it)) },
        )

        HorizontalDivider()

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(12.dp),
        ) {
            // Colonne gauche : aperçu pixel
            // ↓ Appel de TON composable existant (renderer pixel), pas récursif
            AnimationFrame(
                frame = frame,
                sprite = sprite,
                animWidth = animWidth,
                animHeight = animHeight,
                spotSize = 2.dp,
            )

            // Colonne droite : contrôles
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SpriteFramePicker(
                        spriteFrameIndex = frame.spriteFrameIndex,
                        lastIndex = sprite.frames.lastIndex,
                        onIndexChange = { sendIntent(SetSpriteFrameIntent(index, it)) },
                        modifier = Modifier.weight(1f),
                    )
                    BrightnessSlider(
                        brightness = frame.brightness,
                        onBrightnessChange = { sendIntent(SetBrightnessIntent(index, it)) },
                        modifier = Modifier.weight(1f),
                    )
                }

                HorizontalDivider()

                PalettePicker(
                    frameIndex = index,
                    paletteIndex = frame.paletteIndex,
                    palettes = sprite.palettes,
                    sendIntent = sendIntent,
                )

                HorizontalDivider()

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OffsetControl(
                        label = stringResource(Res.string.animation_horizontal_offset_label),
                        value = frame.offsetX,
                        onDecrement = { sendIntent(SetHorizontalOffsetIntent(index, -1)) },
                        onIncrement = { sendIntent(SetHorizontalOffsetIntent(index, 1)) },
                        modifier = Modifier.weight(1f),
                    )
                    OffsetControl(
                        label = stringResource(Res.string.animation_vertical_offset_label),
                        value = frame.offsetY,
                        onDecrement = { sendIntent(SetVerticalOffsetIntent(index, -1)) },
                        onIncrement = { sendIntent(SetVerticalOffsetIntent(index, 1)) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun FrameHeader(
    index: Int,
    durationMs: Int,
    onDurationChange: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = stringResource(Res.string.animation_frame_index_label, index),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        DurationField(
            durationMs = durationMs,
            onDurationChange = onDurationChange,
        )
    }
}

// ─── Durée (TextField compact, width fixe) ───────────────────────────────────

@Composable
private fun DurationField(
    durationMs: Int,
    onDurationChange: (Int) -> Unit,
) {
    var text by remember(durationMs) { mutableStateOf(durationMs.toString()) }

    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            if (input.all { it.isDigit() }) text = input
            input.toIntOrNull()?.let { v -> if (v >= 40) onDurationChange(v) }
        },
        singleLine = true,
        label = { Text(stringResource(Res.string.animation_duration_label)) },
        suffix = { Text("ms") },
        isError = text.toIntOrNull()?.let { it < 40 } ?: true,
        supportingText = {
            if (text.toIntOrNull()?.let { it < 40 } == true) {
                Text(stringResource(Res.string.animation_duration_min_error))
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.width(130.dp),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
    )
}

// ─── Frame du sprite ──────────────────────────────────────────────────────────

@Composable
private fun SpriteFramePicker(
    spriteFrameIndex: Int,
    lastIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember(spriteFrameIndex) { mutableStateOf(spriteFrameIndex.toString()) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Text(
            text = stringResource(Res.string.animation_sprite_frame_index_label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = text,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) text = input
                input.toIntOrNull()
                    ?.coerceIn(0, lastIndex)
                    ?.let { onIndexChange(it) }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = { Text("0 – $lastIndex") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ─── Luminosité ───────────────────────────────────────────────────────────────

@Composable
private fun BrightnessSlider(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.animation_brightness_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "%.2f".format(brightness),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Slider(
            value = brightness,
            onValueChange = onBrightnessChange,
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ─── Palette picker ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PalettePicker(
    frameIndex: Int,
    paletteIndex: Int,
    palettes: List<Palette>,
    sendIntent: (AnimationCreationIntent) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(Res.string.animation_palette_label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            // Bouton fermé : même rendu qu'un item du menu
            Surface(
                shape = MaterialTheme.shapes.extraSmall,
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    palettes.getOrNull(paletteIndex)?.let { palette ->
                        PaletteRowContent(
                            index = paletteIndex,
                            palette = palette,
                            selected = false,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                palettes.forEachIndexed { index, palette ->
                    DropdownMenuItem(
                        text = {
                            PaletteRowContent(
                                index = index,
                                palette = palette,
                                selected = index == paletteIndex,
                            )
                        },
                        onClick = {
                            sendIntent(SetPaletteIntent(frameIndex, index))
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

// Contenu partagé : index + bande de couleurs
@Composable
private fun PaletteRowContent(
    index: Int,
    palette: Palette,
    selected: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = index.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(20.dp),
        )
        PaletteStrip(palette = palette, swatchSize = 12.dp)
    }
}

// ─── Bande de couleurs ────────────────────────────────────────────────────────

@Composable
private fun PaletteStrip(
    palette: Palette,
    swatchSize: Dp,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        palette.colors.forEach { colorInt ->
            Box(
                modifier = Modifier
                    .size(width = swatchSize, height = swatchSize * 1.4f)
                    // Color(Int) fonctionne directement avec un ARGB packed.
                    // Si tes couleurs sont en RGB sans alpha, utilise :
                    // Color(colorInt or (0xFF shl 24))
                    .background(
                        color = Color(colorInt),
                        shape = RoundedCornerShape(2.dp),
                    ),
            )
        }
    }
}

// ─── Offset ───────────────────────────────────────────────────────────────────

@Composable
private fun OffsetControl(
    label: String,
    value: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        CompactStepper(
            label = value.toString(),
            onDecrement = onDecrement,
            onIncrement = onIncrement,
        )
    }
}

// ─── Stepper réutilisable ─────────────────────────────────────────────────────
// Texte "−" / "+" au lieu d'icônes pour éviter material-icons-extended.

@Composable
private fun CompactStepper(
    label: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    decrementEnabled: Boolean = true,
    incrementEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        FilledTonalButton(
            onClick = onDecrement,
            enabled = decrementEnabled,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.defaultMinSize(minWidth = 36.dp, minHeight = 36.dp),
        ) {
            Text(text = "−", style = MaterialTheme.typography.labelLarge)
        }
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        FilledTonalButton(
            onClick = onIncrement,
            enabled = incrementEnabled,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.defaultMinSize(minWidth = 36.dp, minHeight = 36.dp),
        ) {
            Text(text = "+", style = MaterialTheme.typography.labelLarge)
        }
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
                    palette = sprite.palettes[0],
                    width = sprite.width,
                    height = sprite.height,
                    spotSize = 2.dp,
                )
            }
        }
    }
}

@Composable
private fun TopButtonsBlock(
    sprite: Sprite?,
    animation: Animation,
    sendIntent: (AnimationCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var widthStr by remember(animation.width) { mutableStateOf(animation.width.takeIf { it > 0 } ?.toString() ?: "") }
    var heightStr by remember(animation.height) { mutableStateOf(animation.height.takeIf { it > 0 } ?.toString() ?: "") }
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
        if (sprite != null) {
            TextField(
                value = widthStr,
                onValueChange = {
                    if (it.all { c -> c.isDigit() }) widthStr = it
                    it.toIntOrNull()?.let { width ->
                        sendIntent(SetAnimationSizeIntent(width = width))
                    }
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.width_field_label)
                    )
                }
            )
            Text(
                text = "x",
            )
            TextField(
                value = heightStr,
                onValueChange = {
                    if (it.all { c -> c.isDigit() }) heightStr = it
                    it.toIntOrNull()?.let { height ->
                        sendIntent(SetAnimationSizeIntent(height = height))
                    }
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.height_field_label)
                    )
                }
            )
        }
    }
}

private fun Channel<AnimationCreationIntent>.sendIntent(scope: CoroutineScope, intent: AnimationCreationIntent) {
    scope.launch { send(intent) }
}
