package io.github.easyshimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
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
    shimmerOptions: ShimmerOptions = ShimmerOptions.DEFAULT,
) = this.then(
    DrawShimmerElement(
        visible = visible,
        enableFillMaxWidth = enableFillMaxWidth, shimmerOptions
    )
)

private data class DrawShimmerElement(
    val visible: Boolean,
    val enableFillMaxWidth: Boolean,
    val shimmerOptions: ShimmerOptions,
) : ModifierNodeElement<DrawShimmerModifier>() {
    override fun create() = DrawShimmerModifier(
        visible = visible,
        enableFillMaxWidth = enableFillMaxWidth,
        shimmerOptions = shimmerOptions,
    )

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
    private val shimmerOptions: ShimmerOptions,
) : Modifier.Node(), DrawModifierNode, LayoutModifierNode {

    var visible: Boolean = visible
        set(value) {
            field = value
            if (!field && effectAnimatable.isRunning) {
                coroutineScope.launch {
                    effectAnimatable.stop()
                }
            }
        }

    private val colors: List<Color> = shimmerOptions.colors

    private val effectAnimatable by mutableStateOf(Animatable(0f))

    override fun onAttach() {
        super.onAttach()
        if (visible) {
            coroutineScope.launch {
                effectAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = shimmerOptions.animationSpec
                )
            }
        } else {
            coroutineScope.launch {
                effectAnimatable.stop()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (effectAnimatable.isRunning) {
            coroutineScope.launch {
                effectAnimatable.stop()
            }
        }
    }

    override fun ContentDrawScope.draw() {
        if (visible) {
            animatedDraw(
                effectAnimatable = effectAnimatable,
                colors = colors
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