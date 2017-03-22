lazy val root = Project("prometheus_client_scala", file("."))
  .settings(Settings())
  .settings(resolvers := Resolvers())
  .settings(libraryDependencies ++= Dependencies())
