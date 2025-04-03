pluginManagement {
    repositories {
//        google {
//            content {
//                includeGroupByRegex("com\\.android.*")
//                includeGroupByRegex("com\\.google.*")
//                includeGroupByRegex("androidx.*")
//            }
//        }
//        mavenCentral()
//        gradlePluginPortal()

        maven { setUrl("https://repo.huaweicloud.com/repository/maven") }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        google()
//        mavenCentral()

        maven { setUrl("https://repo.huaweicloud.com/repository/maven") }
    }
}

rootProject.name = "Radarny"
include(":app")
include(":radarny")
