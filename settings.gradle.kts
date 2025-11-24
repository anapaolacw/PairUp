// Apply the init script for SSL handling
apply(from = "init.gradle")

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
        maven {
            url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        }
        // Allow all repositories to use HTTP if needed
        all {
            if (this is UrlArtifactRepository) {
                isAllowInsecureProtocol = true
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Allow all repositories to use HTTP if needed
        all {
            if (this is UrlArtifactRepository) {
                isAllowInsecureProtocol = true
            }
        }
    }
}

rootProject.name = "PairUp"
include(":app")
