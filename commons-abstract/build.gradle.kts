plugins {
  id("java-library")
}

dependencies {
    compileOnly(libs.annotations)
    compileOnly(libs.slf4j.api)
    compileOnly(libs.configurate)

    testImplementation(libs.slf4j.api)
    testImplementation(libs.configurate)
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed")
    }
}