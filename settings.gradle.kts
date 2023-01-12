rootProject.name = "micronaut-data-example"

pluginManagement.resolutionStrategy.eachPlugin {
    val kotlinVersion: String by settings
    if (requested.id.id.startsWith("org.jetbrains.kotlin.")) {
        useVersion(kotlinVersion)
    }
}
