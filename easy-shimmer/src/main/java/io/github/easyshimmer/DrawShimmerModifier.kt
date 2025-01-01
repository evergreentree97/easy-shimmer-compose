package io.github.easyshimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.launch

fun Modifier.drawShimmer(
    visible: Boolean,
    enableFillMaxWidth: Boolean = true,
) = this.then(DrawShimmerElement(visible, enableFillMaxWidth))

private data class DrawShimmerElement(
    val visible: Boolean,
    val enableFillMaxWidth: Boolean,
) : ModifierNodeElement<DrawShimmerModifier>() {
    override fun create() = DrawShimmerModifier(visible, enableFillMaxWidth)

    override fun update(node: DrawShimmerModifier) {
        node.visible = visible
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "drawShimmer"
        properties["visible"] = visible
    }
}

internal class DrawShimmerModifier(
    visible: Boolean,
    private val enableFillMaxWidth: Boolean,
) : Modifier.Node(), DrawModifierNode, LayoutModifierNode {

    var visible: Boolean = visible
        set(value) {
            field = value
            if (!field && anim.isRunning) {
                coroutineScope.launch {
                    anim.stop()
                }
            }
        }

    private val colors: List<Color> = listOf(
        Color(0xFFD0D0D0),
        Color(0xFFE3E3E3),
        Color(0xFFD0D0D0)
    )

    private val anim by mutableStateOf(Animatable(0f))

    override fun onAttach() {
        super.onAttach()
        if (visible) {
            coroutineScope.launch {
                anim.animateTo(
                    targetValue = 1f, animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        } else {
            coroutineScope.launch {
                anim.stop()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (anim.isRunning) {
            coroutineScope.launch {
                anim.stop()
            }
        }
    }

    override fun ContentDrawScope.draw() {
        if (visible) {
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
        } else {
            drawContent()
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val childConstraints = if (enableFillMaxWidth) {
            constraints.copy(
                minWidth = constraints.maxWidth,
                maxWidth = constraints.maxWidth
            )
        } else {
            constraints
        }

        val placeable = measurable.measure(childConstraints)

        return layout(
            width = placeable.width,
            height = placeable.height
        ) {
            placeable.place(0, 0)
        }
    }
}