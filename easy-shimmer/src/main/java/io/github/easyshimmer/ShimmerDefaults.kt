package io.github.easyshimmer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import io.github.easyshimmer.ShimmerDefaults.defaultShimmerOptions

/**
 * A global singleton object to manage and provide default [ShimmerOptions].
 *
 * - [defaultShimmerOptions] holds the shimmer configuration that will be used by default.
 * - Set a custom shimmer configuration at application startup (e.g., in your [Application] class)
 *   so that any calls to `rememberShimmerImagePainter` without explicit options will use this default.
 *
 * Example usage:
 * ```
 * // In your Application class
 * class MyApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         ShimmerDefaults.defaultShimmerOptions = ShimmerOptions(
 *             // Customize colors, animation specs, etc.
 *         )
 *     }
 * }
 *
 */
object ShimmerDefaults {
    /**
     * The globally used default [ShimmerOptions].
     *
     * You can override its value at application startup or whenever needed to change
     * the shimmer effect globally.
     */
    var defaultShimmerOptions: ShimmerOptions = ShimmerOptions(
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