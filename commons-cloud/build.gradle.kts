plugins {
  id("java-library")
}

dependencies {
  compileOnly(project(":commons-abstract"))
  compileOnly(libs.cloud.core)
  compileOnly(libs.cloud.annotations)
  compileOnly(libs.cloud.paper)
  compileOnly(libs.paper.api)
}