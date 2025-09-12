subprojects {
  group = "io.github.ynverxe"
  version = "0.1.0"

  plugins.withId("java-library") {
    apply<MavenPublishPlugin>()
  }

  plugins.withId("maven-publish") {
    var publishing = extensions.getByType<PublishingExtension>()
    publishing.publications {
      create<MavenPublication>("defaultMaven") {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()

        from(components["java"])
      }
    }
  }
}