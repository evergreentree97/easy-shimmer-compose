package io.github.easyshimmer.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /* This refers to the state of the View, and it can be implemented as a ViewModel State pattern.*/
            var sampleImageUrl by remember { mutableStateOf("") }
            var sampleText by remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                //	This delay refers to I/O operations, such as network communication.
                delay(2000L)
                sampleImageUrl = "https://plus.unsplash.com/premium_photo-1673765123739-3862ccaeb3d6?q=80&w=3000&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                sampleText = "after loading"
            }

            SampleScreen(
                imageUrl = sampleImageUrl,
                text = sampleText
            )
        }
    }
}