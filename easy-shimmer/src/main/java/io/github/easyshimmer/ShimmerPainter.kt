package io.github.easyshimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
    val shimmerPainter = remember { ShimmerPainter(shimmerOptions) }

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

class ShimmerPainter(
    private val shimmerOptions: ShimmerOptions,
) : Painter() {

    private val effectAnimatable by mutableStateOf(Animatable(0f))

    private val colors: List<Color> = shimmerOptions.colors
    suspend fun start() {
        effectAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = shimmerOptions.animationSpec
        )
    }

    suspend fun stop() {
        effectAnimatable.stop()
    }

    private var drawScopeSize: Size? = null
    override val intrinsicSize: Size
        get() = drawScopeSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        drawScopeSize = size

        animatedDraw(
            effectAnimatable = effectAnimatable,
            colors = colors
        )
    }
}