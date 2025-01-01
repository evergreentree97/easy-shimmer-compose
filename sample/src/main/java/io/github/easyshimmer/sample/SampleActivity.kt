package io.github.easyshimmer.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.easyshimmer.rememberShimmerImagePainter

class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Image(
                modifier = Modifier.size(300.dp),
                painter = rememberShimmerImagePainter(""),
                contentDescription = null
            )
        }
    }
}