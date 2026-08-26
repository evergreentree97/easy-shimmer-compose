package io.github.easyshimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp

/**
 * Draws a shimmering effect by animating a gradient across the current [DrawScope.size].
 * The gradient is built from the colors of [shimmerOptions] and laid out according to its
 * [ShimmerBrush], and it is moved by the current value of [effectAnimatable].
 *
 * @param visibleAnimatable An [Animatable] controlling the shimmer visibility. If this value
 * is null, the shimmer area will be displayed immediately without a visible animation.
 * @param effectAnimatable An [Animatable] controlling the shimmer position. As its value
 * progresses from 0f to 1f, the gradient moves across the drawing area.
 * @param shimmerOptions The colors and the kind of gradient to draw them with.
 */
internal fun DrawScope.animatedDraw(
    visibleAnimatable: Animatable<Float, AnimationVector1D>? = null,
    effectAnimatable: Animatable<Float, AnimationVector1D>,
    shimmerOptions: ShimmerOptions,
) {
    val progress = effectAnimatable.value
    val colors = shimmerOptions.colors

    val brush = when (shimmerOptions.brush) {
        ShimmerBrush.Linear -> linearBrush(colors, progress)
        ShimmerBrush.Sweep -> sweepBrush(colors, progress)
        ShimmerBrush.Radial -> radialBrush(colors, progress)
    }

    drawRect(
        brush = brush,
        size = size,
        alpha = visibleAnimatable?.value ?: 1f
    )
}

/**
 * Builds a gradient band that travels diagonally from beyond the top left corner to beyond
 * the bottom right one as [progress] goes from 0f to 1f.
 */
private fun DrawScope.linearBrush(
    colors: List<Color>,
    progress: Float,
): Brush {
    val start = Offset(
        -size.width + (size.width * 2f * progress),
        -size.height + (size.height * 2f * progress)
    )

    return Brush.linearGradient(
        colors = colors,
        start = start,
        end = Offset(start.x + size.width, start.y + size.height)
    )
}

/**
 * Builds a gradient that completes one turn around the center as [progress] goes from 0f to 1f.
 * The color stops are rotated rather than the canvas, so the whole drawing area stays covered.
 * The colors are treated as a loop, meaning the last one meets the first one again.
 */
private fun DrawScope.sweepBrush(
    colors: List<Color>,
    progress: Float,
): Brush {
    if (colors.size < 2) {
        return Brush.sweepGradient(colors, center = center)
    }

    val shift = progress % 1f
    val edgeColor = colorAt(colors, 1f - shift)

    val stops = colors
        .dropLast(1)
        .mapIndexed { index, color -> ((index.toFloat() / colors.lastIndex) + shift) % 1f to color }
        .filter { (position, _) -> position > 0f && position < 1f }
        .sortedBy { (position, _) -> position }

    return Brush.sweepGradient(
        colorStops = (listOf(0f to edgeColor) + stops + listOf(1f to edgeColor)).toTypedArray(),
        center = center
    )
}

/**
 * Builds a round highlight travelling diagonally across the drawing area, entering beyond the
 * top left corner and leaving beyond the bottom right one.
 */
private fun DrawScope.radialBrush(
    colors: List<Color>,
    progress: Float,
): Brush {
    val radius = size.maxDimension / 2f

    return Brush.radialGradient(
        colors = colors,
        center = Offset(
            x = -radius + ((size.width + (radius * 2f)) * progress),
            y = -radius + ((size.height + (radius * 2f)) * progress)
        ),
        radius = radius
    )
}

/**
 * Samples [colors] at [fraction] of the gradient, where 0f is the first color and 1f the last.
 */
private fun colorAt(
    colors: List<Color>,
    fraction: Float,
): Color {
    val position = (fraction % 1f) * colors.lastIndex
    val index = position.toInt()

    return lerp(
        start = colors[index],
        stop = colors[(index + 1).coerceAtMost(colors.lastIndex)],
        fraction = position - index
    )
}
