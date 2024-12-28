package io.github.coilskeletonpainter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import io.github.skeleton_painter_core.rememberMyPainter

class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            rememberMyPainter()
            Text("샘플")
        }
    }
}