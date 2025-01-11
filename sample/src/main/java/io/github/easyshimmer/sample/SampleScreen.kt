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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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

@Composable
internal fun SampleScreen(
    imageUrl: String,
    text: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Any Image composable that uses rememberShimmerImagePainter will display a Shimmer effect while loading.
        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .size(200.dp),
            painter = rememberShimmerImagePainter(imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        // 	You can modify options like animationSpec and colors, as demonstrated in the following example.
        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .size(200.dp),
            painter = rememberShimmerImagePainter(
                model = imageUrl,
                shimmerOptions = ShimmerOptions(
                    shimmerAnimationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    crossFadeAnimationSpec = tween(600),
                    colors = listOf(
                        Color.Gray.copy(alpha = 0.8f),
                        Color.Gray.copy(alpha = 0.4f),
                        Color.Gray.copy(alpha = 0.8f),
                    )
                )
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        /*
        *  Here is an example of using drawShimmer with a Text. You can also use it with other composables, such as Box, Row, or Column.
        *  By default, the enableFillMaxWidth parameter is set to true,
        *  causing the shimmer effect to fill the width of its parent. You can adjust this by applying padding.
        * */
        Text(
            modifier = Modifier.drawShimmer(
                visible = text.isBlank(),
            ),
            text = text,
            textAlign = TextAlign.Center,
        )

        // 	If you don’t want to apply the default FillMaxWidth Shimmer option, you must set enableFillMaxWidth to false.
        Text(
            modifier = Modifier
                .widthIn(min = 100.dp)
                .drawShimmer(
                    visible = text.isBlank(),
                    enableFillMaxWidth = false,
                ),
            text = text,
            textAlign = TextAlign.Center,
        )

        // 	You can modify options like animationSpec and colors, as demonstrated in the following example.
        Text(
            modifier = Modifier.drawShimmer(
                visible = text.isBlank(),
                shimmerOptions = ShimmerOptions(
                    shimmerAnimationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    crossFadeAnimationSpec = tween(600),
                    colors = listOf(
                        Color.Blue,
                        Color.Blue.copy(0.5f),
                        Color.Blue,
                    )
                )
            ),
            text = text,
            textAlign = TextAlign.Center,
        )
    }
}