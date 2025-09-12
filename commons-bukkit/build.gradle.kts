plugins {
  id("java-library")
}

dependencies {
  compileOnly(project(":commons-abstract"))
  compileOnly(libs.annotations)
  compileOnly(libs.paper.api)
  testImplementation(libs.mock.bukkit)
}