package io.github.easyshimmer

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.ui.graphics.Color

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
)