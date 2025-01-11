package io.github.easyshimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter.State
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

/**
 * Returns a [Painter] that applies a shimmer effect while the image is loading.
 * Internally uses [rememberAsyncImagePainter] to load the image.
 *
 * @param model The data model representing the image to load, which could be a URL or file path.
 * @param placeholder A [Painter] to be displayed while the image is loading, or if none is supplied,
 * a [ShimmerPainter] is used.
 * @param error A [Painter] to display if the image request fails.
 * @param fallback A [Painter] used if the requested image is not available. Defaults to [error].
 * @param onLoading Callback invoked with a [State.Loading] when the image request starts loading.
 * @param onSuccess Callback invoked with a [State.Success] when the image request completes successfully.
 * @param onError Callback invoked with a [State.Error] if the image request fails for any reason.
 * @param contentScale Defines how the image should be scaled within its bounds.
 * @param filterQuality Controls the sampling algorithm applied to the image when scaling.
 * @param shimmerOptions Configuration for the shimmer animation, defaults to [ShimmerOptions.DEFAULT].
 * @return A [Painter] that displays the loaded image or applies a shimmer effect while loading.
 */
@Composable
@NonRestartableComposable
fun rememberShimmerImagePainter(
    model: Any?,
    placeholder: Painter? = null,
    error: Painter? = null,
    fallback: Painter? = error,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
    shimmerOptions: ShimmerOptions = ShimmerOptions.DEFAULT,
): Painter {

    val coroutineScope = rememberCoroutineScope()
    val shimmerPainter = remember(shimmerOptions) { ShimmerPainter(shimmerOptions) }

    return rememberAsyncImagePainter(
        model = model,
        placeholder = placeholder ?: shimmerPainter,
        error = error,
        fallback = fallback,
        onLoading = {
            coroutineScope.launch { shimmerPainter.start() }
            onLoading?.invoke(it)
        },
        onSuccess = {
            coroutineScope.launch { shimmerPainter.stop() }
            onSuccess?.invoke(it)
        },
        onError = {
            coroutineScope.launch { shimmerPainter.stop() }
            onError?.invoke(it)
        },
        contentScale = contentScale,
        filterQuality = filterQuality
    )
}

/**
 * A custom [Painter] implementation that draws a shimmering effect on its content. The shimmer
 * effect is controlled by an [Animatable] that interpolates a value from 0f to 1f using the
 * provided [shimmerOptions]. Once `start()` is called, the animation runs until it reaches the
 * target value, and can be cancelled at any time by calling `stop()`.
 *
 * Usage:
 * 1. Create an instance of [ShimmerPainter] by passing in the desired [ShimmerOptions].
 * 2. Use `start()` to begin the shimmer animation.
 * 3. Call `stop()` to halt the animation whenever needed.
 *
 * @param shimmerOptions defines how the shimmer effect should behave, including its colors
 * and animation spec.
 *
 * @constructor Creates a [ShimmerPainter] that can be used to draw a shimmer effect using
 * the provided [shimmerOptions].
 *
 * @see ShimmerOptions
 * @see Animatable
 * @see Painter
 */
private class ShimmerPainter(
    private val shimmerOptions: ShimmerOptions,
) : Painter() {

    /**
     * The [Animatable] that holds the animation progress of the shimmer effect. This value
     * animates from 0f to 1f when [start] is called.
     */
    private val effectAnimatable by mutableStateOf(Animatable(0f))

    /**
     * The list of [Color]s used to draw the shimmer effect.
     */
    private val colors: List<Color> = shimmerOptions.colors

    /**
     * Initiates the shimmer animation by animating [effectAnimatable] from 0f to 1f.
     */
    suspend fun start() {
        effectAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = shimmerOptions.shimmerAnimationSpec
        )
    }

    /**
     * Stops the shimmer animation immediately, halting the [effectAnimatable] in its current state.
     */
    suspend fun stop() {
        effectAnimatable.stop()
    }

    /**
     * Keeps track of the [Size] within the current [DrawScope]. Used to calculate the painter's
     * [intrinsicSize].
     */
    private var drawScopeSize: Size? = null

    /**
     * Returns the size of this painter if a [DrawScope] has been set; otherwise returns
     * [Size.Unspecified].
     */
    override val intrinsicSize: Size
        get() = drawScopeSize ?: Size.Unspecified

    /**
     * The core drawing method that applies the shimmer effect. It updates the [drawScopeSize]
     * with the current [DrawScope.size] and calls the [animatedDraw] function to render
     * the shimmer overlay.
     */
    override fun DrawScope.onDraw() {
        drawScopeSize = size

        animatedDraw(
            effectAnimatable = effectAnimatable,
            colors = colors
        )
    }
}