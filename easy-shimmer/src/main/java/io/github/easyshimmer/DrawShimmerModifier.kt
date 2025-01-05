package io.github.easyshimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
     * Whether the shimmer effect is currently visible.
     */
    var visible: Boolean = visible
        set(value) {
            if (field == value) return
            field = value
            if (isAttached) {
                handleAnimations()
            }
        }

    /**
     * Control shimmer effect animation and shimmer visibility animation based on the visible state.
     */
    private fun handleAnimations() {
        if (visible) {
            coroutineScope.launch {
                launch {
                    shimmerEffectAnimatable.snapTo(0f)
                    shimmerEffectAnimatable.animateTo(
                        targetValue = 1f,
                        animationSpec = shimmerOptions.shimmerAnimationSpec
                    )
                }
                launch {
                    shimmerVisibleAnimatable.animateTo(
                        targetValue = 1f,
                        animationSpec = shimmerOptions.crossFadeAnimationSpec
                    )
                }
            }
        } else {
            coroutineScope.launch {
                launch {
                    shimmerEffectAnimatable.stop()
                }
                launch {
                    shimmerVisibleAnimatable.animateTo(
                        targetValue = 0f,
                        animationSpec = shimmerOptions.crossFadeAnimationSpec
                    )
                }
            }
        }
    }

    /**
     * The list of gradient [Color] values used for the shimmer effect.
     */
    private val colors: List<Color> = shimmerOptions.colors

    /**
     * An [Animatable] controlling the progress of the visible animation of the shimmer.
     */
    private val shimmerVisibleAnimatable by mutableStateOf(Animatable(0f))

    /**
     * An [DerivedState] controlling the progress of the visible animation of the content.
     */
    private val contentVisibleAnimProgress by derivedStateOf { 1f - shimmerVisibleAnimatable.value }

    /**
     * An [Animatable] controlling the progress of the shimmer animation.
     */
    private val shimmerEffectAnimatable by mutableStateOf(Animatable(0f))

    /**
     * A [Paint] controlling the alpha value for the visibility animation of the content.
     */
    private val contentLayerPaint = Paint()

    /**
     * Called when this node is attached to the composition. If [visible] is true,
     * the shimmer animation begins.
     */
    override fun onAttach() {
        super.onAttach()
        handleAnimations()
    }

    /**
     * Called when this node is detached from the composition. Stops any running
     * shimmer animation.
     */
    override fun onDetach() {
        super.onDetach()
        if (shimmerEffectAnimatable.isRunning) {
            coroutineScope.launch {
                shimmerEffectAnimatable.stop()
            }
        }
        if (shimmerVisibleAnimatable.isRunning) {
            coroutineScope.launch {
                shimmerVisibleAnimatable.stop()
            }
        }
    }

    /**
     * Draws either the shimmer effect when [visible] is true, or the normal content
     * otherwise.
     */
    override fun ContentDrawScope.draw() {
        if (contentVisibleAnimProgress > 0f) {
            drawIntoCanvas { canvas ->
                canvas.saveLayer(
                    bounds = size.toRect(),
                    paint = contentLayerPaint.apply {
                        alpha = contentVisibleAnimProgress
                    }
                )
                drawContent()
                canvas.restore()
            }
        }

        if (shimmerVisibleAnimatable.value > 0f) {
            animatedDraw(
                visibleAnimatable = shimmerVisibleAnimatable,
                effectAnimatable = shimmerEffectAnimatable,
                colors = colors
            )
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