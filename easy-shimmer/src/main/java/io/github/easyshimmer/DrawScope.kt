package io.github.easyshimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Draws a shimmering effect by animating a linear gradient across the current [DrawScope.size].
 * The gradient is built from [colors], and its start and end positions are calculated
 * based on the current value of [effectAnimatable], creating a moving highlight effect.
 *
 * @param effectAnimatable An [Animatable] controlling the shimmer position. As its value
 * progresses from 0f to 1f, the gradient shifts diagonally across the drawing area.
 * @param colors A list of [Color] values used to construct the gradient.
 */
internal fun DrawScope.animatedDraw(
    effectAnimatable: Animatable<Float, AnimationVector1D>,
    colors: List<Color>,
) {
    val start = Offset(
        -size.width + (size.width * 2f * effectAnimatable.value),
        -size.height + (size.height * 2f * effectAnimatable.value)
    )

    val end = Offset(start.x + size.width, start.y + size.height)

    val brush = Brush.linearGradient(
        colors = colors,
        start = start,
        end = end
    )

    drawRect(
        brush = brush,
        size = size
    )
}