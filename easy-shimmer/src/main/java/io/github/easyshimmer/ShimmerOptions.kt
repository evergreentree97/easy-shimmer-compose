package io.github.easyshimmer

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import io.github.easyshimmer.ShimmerOptions.Companion.DEFAULT

/**
 * Holds the configuration for a shimmer effect, including the animation specification
 * and the colors used. The [DEFAULT] instance provides a basic shimmering animation
 * with gray tones cycling indefinitely.
 *
 * @property shimmerAnimationSpec The [AnimationSpec] that defines how the shimmering animation
 * is interpolated over time.
 * @property crossFadeAnimationSpec The [AnimationSpec] that defines how the cross fading animation
 * is interpolated over time.
 * @property colors A list of [Color] values used to construct the shimmer gradient.
 */
data class ShimmerOptions(
    val shimmerAnimationSpec: AnimationSpec<Float>,
    val crossFadeAnimationSpec: AnimationSpec<Float>,
    val colors: List<Color>
) {
    companion object {
        /**
         * A default configuration for shimmer, featuring a 3-second repeating cycle
         * of gray tones. The animation restarts after each cycle, creating a continuous
         * shimmer effect.
         */
        val DEFAULT = ShimmerOptions(
            shimmerAnimationSpec = infiniteRepeatable(
                animation = tween(3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            crossFadeAnimationSpec = tween(600),
            colors = listOf(
                Color(0xFFD0D0D0),
                Color(0xFFE3E3E3),
                Color(0xFFD0D0D0)
            )
        )
    }
}