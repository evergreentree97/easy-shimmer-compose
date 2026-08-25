package io.github.easyshimmer

/**
 * The gradient the shimmer effect is drawn with. Every entry animates over the same
 * 0f to 1f progress, but interprets it differently.
 *
 * @property Linear A band of colors travelling diagonally across the drawing area.
 * @property Sweep A highlight rotating once around the center.
 * @property Radial A round highlight travelling diagonally across the drawing area.
 */
enum class ShimmerBrush {
    Linear,
    Sweep,
    Radial,
}
