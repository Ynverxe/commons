dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)

    repositories {
        mavenCentral()
        mavenLocal()

        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}

rootProject.name = "commons"

include("commons-configuration")
include("commons-bukkit")
include("commons-abstract")
include("commons-cloud")