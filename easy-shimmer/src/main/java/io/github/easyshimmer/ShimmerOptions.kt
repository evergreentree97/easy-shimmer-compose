package io.github.easyshimmer

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color

/**
 * Holds the configuration for a shimmer effect, including the animation specification
 * and the colors used. The [DEFAULT] instance provides a basic shimmering animation
 * with gray tones cycling indefinitely.
 *
 * @property animationSpec The [AnimationSpec] that defines how the shimmering animation
 * is interpolated over time.
 * @property colors A list of [Color] values used to construct the shimmer gradient.
 */
data class ShimmerOptions(
    val animationSpec: AnimationSpec<Float>,
    val colors: List<Color>
) {
    companion object {
        /**
         * A default configuration for shimmer, featuring a 3-second repeating cycle
         * of gray tones. The animation restarts after each cycle, creating a continuous
         * shimmer effect.
         */
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