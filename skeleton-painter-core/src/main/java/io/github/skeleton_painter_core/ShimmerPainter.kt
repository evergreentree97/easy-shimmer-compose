package io.github.skeleton_painter_core

import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
): Painter {

    val coroutineScope = rememberCoroutineScope()
    val shimmerPainter = remember { ShimmerPainter() }

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

class ShimmerPainter : Painter() {

    private val anim by mutableStateOf(Animatable(0f))

    private val colors: List<Color> = listOf(
        Color(0xFFD0D0D0),
        Color(0xFFE3E3E3),
        Color(0xFFD0D0D0)
    )

    suspend fun start() {
        anim.animateTo(
            targetValue = 1f, animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    suspend fun stop() {
        anim.stop()
    }

    private var drawScopeSize: Size? = null
    override val intrinsicSize: Size
        get() = drawScopeSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        drawScopeSize = size

        val start = Offset(
            -size.width + (size.width * 2f * anim.value),
            -size.height + (size.height * 2f * anim.value)
        )

        val end = Offset(start.x + size.width, start.y + size.height)

        val brush = Brush.linearGradient(
            colors = colors,
            start = start,
            end = end
        )

        drawRect(
            brush,
            Offset.Zero,
            size
        )
    }
}