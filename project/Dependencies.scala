/*
 * Copyright © 2017 Full 360 Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import sbt.Keys._
import sbt._

object Dependencies {

  // @formatter:off
  def apply() = Seq(libraryDependencies ++= Seq(
    // Core
    "io.prometheus" % "simpleclient" % Versions.prometheus % "compile",
    "io.prometheus" % "simpleclient_hotspot" % Versions.prometheus % "compile",
    "io.prometheus" % "simpleclient_servlet" % Versions.prometheus % "compile",
    "io.prometheus" % "simpleclient_pushgateway" % Versions.prometheus % "compile",
    "org.scala-lang" % "scala-reflect" % Versions.reflect % "compile",

    // Test
    "org.scalatest" %% "scalatest" % Versions.scalatest % "test",
    "org.mockito" % "mockito-core" % Versions.mockito % "test",
    "org.hamcrest" % "hamcrest-all" % Versions.hamcrest % "test",
    "org.specs2" %% "specs2-core" % Versions.specs2 % "test",

    // Akka Http
    "com.typesafe.akka" %% "akka-http" % Versions.akka % "provided",
    "com.typesafe.akka" %% "akka-http-testkit" % Versions.akka % "test",

    // Finatra
    "com.twitter" %% "finatra-http" % Versions.finatra % "provided",
    "com.twitter" %% "finatra-http" % Versions.finatra % "test",
    "com.twitter" %% "finatra-http" % Versions.finatra % "test" classifier "tests",
    "com.twitter" %% "inject-server" % Versions.finatra % "test",
    "com.twitter" %% "inject-server" % Versions.finatra % "test" classifier "tests",
    "com.twitter" %% "inject-app" % Versions.finatra % "test",
    "com.twitter" %% "inject-app" % Versions.finatra % "test" classifier "tests",
    "com.twitter" %% "inject-core" % Versions.finatra % "test",
    "com.twitter" %% "inject-core" % Versions.finatra % "test" classifier "tests",
    "com.twitter" %% "inject-modules" % Versions.finatra % "test",
    "com.twitter" %% "inject-modules" % Versions.finatra % "test" classifier "tests",

    "com.google.inject.extensions" % "guice-testlib" % Versions.guice % "test"
  ))

  private[this] object Versions {
    val prometheus = "0.0.26"
    val reflect = "2.12.2"
    val akka = "10.0.9"
    val finatra = "2.12.0"
    val scalatest = "3.0.4"
    val mockito = "1.9.5"
    val hamcrest = "1.3"
    val specs2 = "4.0.0-RC2"
    val guice = "4.1.0"
  }
  // @formatter:on
}
