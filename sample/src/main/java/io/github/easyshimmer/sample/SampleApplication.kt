package io.github.easyshimmer.sample

import android.app.Application
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import io.github.easyshimmer.ShimmerDefaults
import io.github.easyshimmer.ShimmerOptions

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // This is just an example showing how to override ShimmerDefaults at application launch.
        ShimmerDefaults.defaultShimmerOptions = ShimmerOptions(
            shimmerAnimationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            crossFadeAnimationSpec = tween(1000),
            colors = listOf(
                Color.Gray.copy(alpha = 0.8f),
                Color.Blue.copy(alpha = 0.4f),
                Color.Black.copy(alpha = 0.8f),
            )
        )
    }
}