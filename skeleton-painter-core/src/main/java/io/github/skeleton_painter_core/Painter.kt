package io.github.skeleton_painter_core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberAsyncImagePainter

@Composable
fun rememberMyPainter(): Painter{
    return rememberAsyncImagePainter("")
}