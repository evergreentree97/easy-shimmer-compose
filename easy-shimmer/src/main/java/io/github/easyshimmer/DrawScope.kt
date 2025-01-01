package io.github.easyshimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

fun DrawScope.animatedDraw(
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