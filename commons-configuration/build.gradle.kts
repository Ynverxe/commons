plugins {
  id("java-library")
}

dependencies {
  compileOnly(project(":commons-abstract"))
  compileOnly(libs.configurate.helper)
  compileOnly(libs.configurate)
  compileOnly(libs.annotations)
  compileOnly(libs.slf4j)
  compileOnly(libs.adventure.api)
  compileOnly(libs.adventure.minimessage)
}

tasks.test {
  useJUnitPlatform()
}