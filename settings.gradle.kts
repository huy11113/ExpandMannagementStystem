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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
// settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Buộc dùng kho ở settings
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Thêm JitPack cho MPAndroidChart
    }
}

include(":app") // Thêm mô-đun app nếu chưa có

rootProject.name = "ExpandManagementSystem"
include(":app")
 