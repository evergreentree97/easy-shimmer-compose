package io.github.easyshimmer

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color

data class ShimmerOptions(
    val animationSpec: AnimationSpec<Float>,
    val colors: List<Color>
) {
    companion object {
        val DEFAULT = ShimmerOptions(
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            colors = listOf(
                Color.Gray.copy(alpha = 0.8f),
                Color.Gray.copy(alpha = 0.4f),
                Color.Gray.copy(alpha = 0.8f),
            )
        )
    }
}
