package com.wechantloup.spritehandler.composeElement

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wechantloup.spritehandler.model.Animation
import com.wechantloup.spritehandler.model.Palette
import com.wechantloup.spritehandler.model.Sprite

@Composable
internal fun AnimationFrame(
    frame: Animation.Frame,
    sprite: Sprite,
    animWidth: Int,
    animHeight: Int,
    modifier: Modifier = Modifier,
    spotSize: Dp = 16.dp,
    diffuserBlur: Dp = spotSize * 0.5f,
    diffuserStrength: Float = 0.5f,
) {
    val spriteFrame = sprite.frames[frame.spriteFrameIndex]
    val pixels = remember(spriteFrame, sprite.width, sprite.height, animWidth, animHeight, frame.offsetX, frame.offsetY) {
        applyOffset(
            frame = spriteFrame,
            spriteWidth = sprite.width,
            spriteHeight = sprite.height,
            animWidth = animWidth,
            animHeight = animHeight,
            offsetX = frame.offsetX,
            offsetY = frame.offsetY,
        )
    }
    SpriteFrame(
        frame = pixels,
        palette = sprite.palettes[frame.paletteIndex],
        width = animWidth,
        height = animHeight,
        spotSize = spotSize,
        diffuserBlur = diffuserBlur,
        diffuserStrength = diffuserStrength,
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
    diffuserBlur: Dp = spotSize * 0.5f,
    diffuserStrength: Float = 0.5f,
) {
    val colors = remember(palette) { palette.colors.map { Color(it) } }

    Box(modifier = modifier) {
        LedCanvas(
            frame = frame,
            colors = colors,
            width = width,
            height = height,
            spotSize = spotSize,
            drawBackground = true,
        )
        if (diffuserBlur > 0.dp) {
            LedCanvas(
                frame = frame,
                colors = colors,
                width = width,
                height = height,
                spotSize = spotSize,
                modifier = Modifier
                    .blur(diffuserBlur)
                    .graphicsLayer(alpha = diffuserStrength),
            )
        }
    }
}

@Composable
private fun LedCanvas(
    frame: List<Int>,
    colors: List<Color>,
    width: Int,
    height: Int,
    spotSize: Dp,
    drawBackground: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val spotSizePx = with(LocalDensity.current) { spotSize.toPx() }
    Canvas(
        modifier = modifier.size(spotSize * 2 * width, spotSize * 2 * height),
    ) {
        if (drawBackground) drawRect(Color.Black)
        for (j in 0 until height) {
            for (i in 0 until width) {
                val colorIndex = frame[width * j + i]
                val color = colors[colorIndex]
                if (color.alpha == 0f) continue

                val centerX = (i * 2 + 1) * spotSizePx
                val centerY = (j * 2 + 1) * spotSizePx
                val center = Offset(centerX, centerY)

                drawCircle(
                    color = color.copy(alpha = 0.15f),
                    radius = spotSizePx * 0.9f,
                    center = center,
                )
                drawCircle(
                    color = color.copy(alpha = 0.35f),
                    radius = spotSizePx * 0.65f,
                    center = center,
                )
                drawCircle(
                    color = color,
                    radius = spotSizePx * 0.5f,
                    center = center,
                )
            }
        }
    }
}

private fun applyOffset(
    frame: List<Int>,
    spriteWidth: Int,
    spriteHeight: Int,
    animWidth: Int,
    animHeight: Int,
    offsetX: Int,
    offsetY: Int,
): List<Int> {
    // Ancre bas-gauche : bas du sprite aligné sur le bas de la canvas d'animation,
    // gauche alignée sur la gauche. Aucune ambiguïté odd/even.
    val baseY = spriteHeight - animHeight
    return List(animWidth * animHeight) { index ->
        val destX = index % animWidth
        val destY = index / animWidth
        val srcX = destX - offsetX
        val srcY = destY + baseY - offsetY
        if (srcX in 0 until spriteWidth && srcY in 0 until spriteHeight) {
            frame[srcY * spriteWidth + srcX]
        } else {
            0
        }
    }
}
