package io.github.easyshimmer.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.easyshimmer.rememberShimmerImagePainter

@Composable
internal fun SampleScreen() {
    Image(
        modifier = Modifier.size(300.dp),
        painter = rememberShimmerImagePainter(""),
        contentDescription = null
    )
}