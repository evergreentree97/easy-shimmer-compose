pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven {
            url = uri("https://jitpack.io")
        }
        mavenCentral()
    }
}
rootProject.name = "easy-shimmer-compose"
include(":sample")
include(":easy-shimmer")
