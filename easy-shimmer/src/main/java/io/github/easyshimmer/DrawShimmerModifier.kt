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

/**
 * Applies a shimmer effect to the current [Modifier] when [visible] is true.
 * Optionally forces the content to fill the maximum available width if
 * [enableFillMaxWidth] is set, and uses the specified [shimmerOptions] for
 * customizing animation behavior and colors.
 *
 * @param visible Determines if the shimmer effect is displayed.
 * @param enableFillMaxWidth Forces the composable to fill the maximum width
 * when set to true.
 * @param shimmerOptions Configuration options for the shimmer effect,
 * including animation specs and colors.
 */
fun Modifier.drawShimmer(
    visible: Boolean,
    enableFillMaxWidth: Boolean = true,
    shimmerOptions: ShimmerOptions = ShimmerOptions.DEFAULT,
) = this.then(
    DrawShimmerElement(
        visible = visible,
        enableFillMaxWidth = enableFillMaxWidth,
        shimmerOptions = shimmerOptions
    )
)

/**
 * Represents a [ModifierNodeElement] that creates a [DrawShimmerModifier] to apply
 * a shimmer effect.
 */
private data class DrawShimmerElement(
    val visible: Boolean,
    val enableFillMaxWidth: Boolean,
    val shimmerOptions: ShimmerOptions,
) : ModifierNodeElement<DrawShimmerModifier>() {

    /**
     * Creates and returns a new [DrawShimmerModifier] using the current values
     * of [visible], [enableFillMaxWidth], and [shimmerOptions].
     */
    override fun create() = DrawShimmerModifier(
        visible = visible,
        enableFillMaxWidth = enableFillMaxWidth,
        shimmerOptions = shimmerOptions,
    )

    /**
     * Updates the [node] with any changes to [visible], ensuring the shimmer
     * effect is started or stopped as needed.
     */
    override fun update(node: DrawShimmerModifier) {
        node.visible = visible
    }

    /**
     * Provides inspector properties for debugging. This includes a unique name
     * for the element and any relevant fields to be displayed in tooling.
     */
    override fun InspectorInfo.inspectableProperties() {
        name = "drawShimmer"
        properties["visible"] = visible
    }
}

/**
 * A [Modifier.Node] implementation that applies a shimmering effect by drawing a
 * repeating gradient animation. The shimmer can be enabled or disabled via the
 * [visible] property, and optionally stretched to fill the maximum available width
 * when [enableFillMaxWidth] is true.
 */
internal class DrawShimmerModifier(
    visible: Boolean,
    private val enableFillMaxWidth: Boolean,
    private val shimmerOptions: ShimmerOptions,
) : Modifier.Node(), DrawModifierNode, LayoutModifierNode {

    /**
     * Whether the shimmer effect is currently visible. When changed from true to false,
     * any ongoing animation is stopped.
     */
    var visible: Boolean = visible
        set(value) {
            field = value
            if (!field && effectAnimatable.isRunning) {
                coroutineScope.launch {
                    effectAnimatable.stop()
                }
            }
        }

    /**
     * The list of gradient [Color] values used for the shimmer effect.
     */
    private val colors: List<Color> = shimmerOptions.colors

    /**
     * An [Animatable] controlling the progress of the shimmer animation.
     */
    private val effectAnimatable by mutableStateOf(Animatable(0f))

    /**
     * Called when this node is attached to the composition. If [visible] is true,
     * the shimmer animation begins; otherwise, it's immediately stopped.
     */
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

    /**
     * Called when this node is detached from the composition. Stops any running
     * shimmer animation.
     */
    override fun onDetach() {
        super.onDetach()
        if (effectAnimatable.isRunning) {
            coroutineScope.launch {
                effectAnimatable.stop()
            }
        }
    }

    /**
     * Draws either the shimmer effect when [visible] is true, or the normal content
     * otherwise.
     */
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

    /**
     * Measures the child layout. If [enableFillMaxWidth] is true, the child's minWidth
     * and maxWidth are set to match the constraints' maxWidth; otherwise, the child's
     * constraints remain unchanged.
     */
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