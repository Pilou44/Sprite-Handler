package com.wechantloup.spritehandler.composeElement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wechantloup.spritehandler.model.Animation
import com.wechantloup.spritehandler.model.Palette
import com.wechantloup.spritehandler.model.Sprite

@Composable
internal fun AnimationFrame(
    frame: Animation.Frame,
    sprite: Sprite,
    modifier: Modifier = Modifier,
) {
    val spriteFrame = sprite.frames[frame.spriteFrameIndex]
    val pixels = applyOffset(
        frame = spriteFrame,
        width = sprite.width,
        height = sprite.height,
        offsetX = frame.offsetX,
        offsetY = frame.offsetY,
    )
    SpriteFrame(
        frame = pixels,
        palette = sprite.palette,
        width = sprite.width,
        height = sprite.height,
        spotSize = 2.dp,
        modifier = modifier,
    )
}

@Composable
internal fun SpriteFrame(
    frame: List<Int>,
    palette: Palette,
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    spotSize: Dp = 16.dp,
) {
    Column(
        modifier = modifier.background(Color.Black),
    ) {
        for (j in 0 until height) {
            Row {
                for (i in 0 until width) {
                    val colorIndex = frame[width * j + i]
                    val color = Color(palette.colors[colorIndex])
                    LedPixel(color = color, spotSize = spotSize)
                }
            }
        }
    }
}

@Composable
private fun LedPixel(color: Color, spotSize: Dp) {
    val isOff = color.alpha == 0f
    Box(
        modifier = Modifier.size(spotSize * 2),
        contentAlignment = Alignment.Center,
    ) {
        if (!isOff) {
            // Halo externe
            Box(
                modifier = Modifier
                    .size(spotSize * 1.8f)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
            )
            // Halo interne
            Box(
                modifier = Modifier
                    .size(spotSize * 1.3f)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.35f))
            )
        }
        // Point LED
        Box(
            modifier = Modifier
                .size(spotSize)
                .clip(CircleShape)
                .background(color)
        )
    }
}

private fun applyOffset(
    frame: List<Int>,
    width: Int,
    height: Int,
    offsetX: Int,
    offsetY: Int,
): List<Int> = List(width * height) { index ->
    val destX = index % width
    val destY = index / width
    val srcX = destX - offsetX
    val srcY = destY - offsetY
    if (srcX in 0 until width && srcY in 0 until height) {
        frame[srcY * width + srcX]
    } else {
        0
    }
}
