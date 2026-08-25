# CLAUDE.md

## What this is

A small Compose library that draws shimmer placeholders. Two public entry points:

- `Modifier.drawShimmer(visible, enableFillMaxWidth, shimmerOptions)` in `DrawShimmerModifier.kt`
- `rememberShimmerImagePainter(...)` in `ShimmerPainter.kt`, wrapping Coil's `rememberAsyncImagePainter`

Both take `ShimmerOptions` (colors plus animation specs, defaults in `ShimmerDefaults`) and
share the gradient drawing in `DrawScope.animatedDraw`.

`:easy-shimmer` is the published library, `:sample` is a demo app.

## Build

```
./gradlew :easy-shimmer:assembleRelease
./gradlew :sample:assembleDebug
```

JDK 17, matching `jitpack.yml`. No test source set yet.

## Conventions

Commits and PR titles are prefixed with a bracketed type, in English:

```
[Feat] apply global options
[Fix] Replace the ShimmerPainter with a new one whenever the shimmerOptions change.
[Chore] clean dependencies
[Docs] Add Comments for sample
[Refactor] change file name and modifier
[Release] 0.0.1 setting
```

Branches are `<type>/<snake_case_topic>`, for example `feat/shimmer_defaults` or
`fix/remember_key`. Everything targets `main`.

## Worth knowing

- Every declaration has a KDoc block. Keep it that way.
- Anything in a public signature belongs in `api`, not `implementation`. The library
  keeps its dependency surface small.
- Adding a parameter to a `ModifierNodeElement` means handling it in `update()` too,
  or attached nodes silently keep the old value.
- Don't bump the published version in a feature PR. Releases are their own commit.
