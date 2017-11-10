lazy val root = (project in file("."))
  .aggregate(core, akka, finatra)
  .settings(Settings() ++ Seq(
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))),
    publishLocal := {},
    publish := {})
  )
  .settings(name := "prometheus-client-scala")

lazy val core = (project in file("client-core"))
  .settings(Settings())
  .settings(Publish())
  .settings(name := "prometheus-client-scala-core")
  .settings(libraryDependencies ++= {
    import Dependencies._

    provided(prometheusClient, prometheusCommon, prometheusHotspot) ++
    test(scalatest, mockito)
  })

lazy val akka = (project in file("client-akka"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(Settings())
  .settings(Publish())
  .settings(name := "prometheus-client-scala-akka-http")
  .settings(libraryDependencies ++= {
    import Dependencies._

    provided(akkaHttp, prometheusClient, prometheusCommon, prometheusHotspot) ++
    test(akkaHttpTest, scalatest, mockito)
  })

lazy val finatra = (project in file("client-finatra"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(Settings())
  .settings(Publish())
  .settings(name := "prometheus-client-scala-finatra")
  .settings(libraryDependencies ++= {
    import Dependencies._

    provided(finatraHttp, prometheusClient, prometheusCommon, prometheusHotspot) ++
    test(finatraHttpTest, finatraTestCore, finatraTestServer, finatraTestApp, finatraTestModules, finatraTestGuice, junit, scalatest, mockito) ++
    testClassifier(finatraHttpTest, finatraTestCore, finatraTestServer, finatraTestApp, finatraTestModules)
  })
