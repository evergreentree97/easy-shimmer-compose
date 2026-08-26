package io.github.easyshimmer

import androidx.compose.animation.core.Animatable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.Job
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
    shimmerOptions: ShimmerOptions = ShimmerDefaults.defaultShimmerOptions,
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
     * Hands the latest values to the [node], which invalidates and restarts only what
     * actually changed.
     */
    override fun update(node: DrawShimmerModifier) {
        node.update(
            visible = visible,
            enableFillMaxWidth = enableFillMaxWidth,
            shimmerOptions = shimmerOptions,
        )
    }

    /**
     * Provides inspector properties for debugging. This includes a unique name
     * for the element and any relevant fields to be displayed in tooling.
     */
    override fun InspectorInfo.inspectableProperties() {
        name = "drawShimmer"
        properties["visible"] = visible
        properties["enableFillMaxWidth"] = enableFillMaxWidth
        properties["shimmerOptions"] = shimmerOptions
    }
}

/**
 * A [Modifier.Node] implementation that applies a shimmering effect by drawing a
 * repeating gradient animation. The shimmer can be enabled or disabled via the
 * [visible] property, and optionally stretched to fill the maximum available width
 * when [enableFillMaxWidth] is true.
 */
internal class DrawShimmerModifier(
    private var visible: Boolean,
    private var enableFillMaxWidth: Boolean,
    private var shimmerOptions: ShimmerOptions,
) : Modifier.Node(), DrawModifierNode, LayoutModifierNode {

    /**
     * An [Animatable] controlling the progress of the visible animation of the shimmer.
     */
    private val shimmerVisibleAnimatable = Animatable(0f)

    /**
     * An [Animatable] controlling the progress of the shimmer animation.
     */
    private val shimmerEffectAnimatable = Animatable(0f)

    /**
     * A [Paint] controlling the alpha value for the visibility animation of the content.
     */
    private val contentLayerPaint = Paint()

    /**
     * The job running the animations started by [handleAnimations], kept so that a
     * restart cancels the previous one instead of leaving it to the [Animatable] mutex.
     */
    private var animationJob: Job? = null

    /**
     * Applies the latest values of the element. Every value is stored before anything is
     * invalidated, so the order of the assignments carries no meaning. The animations are
     * restarted when [visible] changed, or when the options changed while the shimmer is
     * visible and the running animation would otherwise keep the old specs.
     */
    fun update(
        visible: Boolean,
        enableFillMaxWidth: Boolean,
        shimmerOptions: ShimmerOptions,
    ) {
        if (this.enableFillMaxWidth != enableFillMaxWidth) {
            this.enableFillMaxWidth = enableFillMaxWidth
            invalidateMeasurement()
        }

        val optionsChanged = this.shimmerOptions != shimmerOptions
        val visibleChanged = this.visible != visible
        this.shimmerOptions = shimmerOptions
        this.visible = visible

        if (optionsChanged) {
            invalidateDraw()
        }
        if (visibleChanged || (optionsChanged && visible)) {
            handleAnimations()
        }
    }

    /**
     * Control shimmer effect animation and shimmer visibility animation based on the visible state.
     */
    private fun handleAnimations() {
        animationJob?.cancel()
        animationJob = coroutineScope.launch {
            if (visible) {
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
            } else {
                shimmerVisibleAnimatable.animateTo(
                    targetValue = 0f,
                    animationSpec = shimmerOptions.crossFadeAnimationSpec
                )
            }
        }
    }

    /**
     * Called when this node is attached to the composition. If [visible] is true,
     * the shimmer animation begins.
     */
    override fun onAttach() {
        super.onAttach()
        handleAnimations()
    }

    /**
     * Draws either the shimmer effect when [visible] is true, or the normal content
     * otherwise.
     */
    override fun ContentDrawScope.draw() {
        val contentAlpha = 1f - shimmerVisibleAnimatable.value

        if (contentAlpha > 0f) {
            drawIntoCanvas { canvas ->
                canvas.saveLayer(
                    bounds = size.toRect(),
                    paint = contentLayerPaint.apply {
                        alpha = contentAlpha
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
                shimmerOptions = shimmerOptions
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
