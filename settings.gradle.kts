pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Kollocol"
include(":app")
include(":data")
include(":domain")
include(":core")
include(":core:common")
include(":core:di")
include(":core:navigation")
include(":core:network")
include(":core:session")
include(":core:ui")
include(":feature")
include(":feature:auth")
include(":feature:main")
 