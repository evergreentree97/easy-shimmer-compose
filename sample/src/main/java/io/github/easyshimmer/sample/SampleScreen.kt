package io.github.easyshimmer.sample

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.easyshimmer.ShimmerOptions
import io.github.easyshimmer.drawShimmer
import io.github.easyshimmer.rememberShimmerImagePainter
import io.github.easyshimmer.shimmerDefaultColors
import kotlinx.coroutines.delay

@Composable
internal fun SampleScreen() {
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000L)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .size(200.dp),
            painter = rememberShimmerImagePainter("https://plus.unsplash.com/premium_photo-1673765123739-3862ccaeb3d6?q=80&w=3000&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .size(200.dp),
            painter = rememberShimmerImagePainter(
                model = "https://plus.unsplash.com/premium_photo-1673765123739-3862ccaeb3d6?q=80&w=3000&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                shimmerOptions = ShimmerOptions(
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    colors = shimmerDefaultColors
                )
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Text(
            modifier = Modifier.drawShimmer(
                visible = isLoading,
                enableFillMaxWidth = false,
            ),
            text = if (isLoading) {
                ""
            } else {
                "easy shimmer compose"
            },
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier.drawShimmer(
                visible = isLoading,
                shimmerOptions = ShimmerOptions(
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    colors = listOf(
                        Color.Blue,
                        Color.Blue.copy(0.5f),
                        Color.Blue,
                    )
                )
            ),
            text = if (isLoading) {
                ""
            } else {
                "easy shimmer compose"
            },
            textAlign = TextAlign.Center,
        )
    }
}