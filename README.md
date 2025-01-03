# 🏞️ EasyShimmerCompose

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=26)

EasyshimmerCompose is a lightweight library that simplifies adding shimmer effects to your Jetpack Compose applications. Leveraging Coil's image loading capabilities, you can easily apply shimmer effects to images, text, or any other composable using the drawShimmer modifier or rememberShimmerImagePainter.

## ✨ Features

*   **Easy to Use**: Apply shimmer effects effortlessly with the `drawShimmer` modifier or `rememberShimmerImagePainter`.
*   **Composable Versatility**:  Supports shimmer effects on various Composables, including images, text, `Box`, `Row`, and `Column`.
*   **Animation Control**: Customize shimmer effects by adjusting `animationSpec` and `colors` through `ShimmerOptions`.
*   **FillMaxWidth Option**: Control the shimmer effect's width to match its parent Composable using the `enableFillMaxWidth` option.
*   **Powered by Coil**: Utilizes Coil for efficient image loading and integration with rememberShimmerImagePainter.

## ⬇️ Download

### Gradle

Add the following dependency to your project's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("io.github.evergreentree97:easy-shimmer-compose:0.0.1") // Replace with the latest version
}
```

## 🛠️ Usage

### `rememberShimmerImagePainter`

`rememberShimmerImagePainter` returns a `Painter` that displays a shimmer effect while an image is loading. Use it with the `Image` composable.

```kotlin
Image(
    modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .size(200.dp),
    painter = rememberShimmerImagePainter(imageUrl),
    contentDescription = null,
    contentScale = ContentScale.Crop,
)
```

### `drawShimmer` Modifier

The `drawShimmer` modifier applies a shimmer effect to any Composable. You can use it with `Text`, `Box`, `Row`, `Column`, and others.
By default, drawShimmer expands to fill the maximum available width thanks to the enableFillMaxWidth option being set to true. You can adjust the shimmer's size by applying padding to the composable:

```kotlin
// This shimmer effect will have horizontal padding, making it narrower than its parent.
Text(
    modifier = Modifier.drawShimmer(
        visible = text.isBlank(),
    ),
    text = text,
    textAlign = TextAlign.Center,
)
```

### `ShimmerOptions`
You can customize your shimmer effect with `ShimmerOptions`
```kotlin
ShimmerOptions(
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
```

## 📱 Sample

**For a detailed implementation and example usage, please refer to the `sample` module in the repository.** The sample module demonstrates how to integrate Easyshimmer into your project and showcases various use cases, including customizing `ShimmerOptions` and handling different loading states.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## ❤️ License

```
Copyright 2025 EvergreenTree97 (Sangrok Choi)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
