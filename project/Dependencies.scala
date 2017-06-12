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
    "io.prometheus"        % "simpleclient"             % "0.0.21"  % "compile",
    "io.prometheus"        % "simpleclient_hotspot"     % "0.0.21"  % "compile",
    "io.prometheus"        % "simpleclient_servlet"     % "0.0.21"  % "compile",
    "io.prometheus"        % "simpleclient_pushgateway" % "0.0.21"  % "compile",
    "org.scala-lang"       % "scala-reflect"            % "2.11.8"  % "compile",

    // Test
    "org.scalatest"       %% "scalatest"                % "2.2.3"   % "test",
    "org.mockito"          % "mockito-core"             % "1.9.5"   % "test",
    "org.hamcrest"         % "hamcrest-all"             % "1.3"     % "test",
    "org.specs2"          %% "specs2"                   % "2.3.12"  % "test",

    // Akka Http
    "com.typesafe.akka"   %% "akka-http-testkit"        % "10.0.5" % "test",
    "com.typesafe.akka"   %% "akka-http"                % "10.0.5" % "provided",

    // Finatra
    "com.twitter.finatra" %% "finatra-http"             % "2.1.6" % "test",
    "com.twitter.finatra" %% "finatra-http"             % "2.1.6" % "test" classifier "tests",
    "com.twitter.inject"  %% "inject-server"            % "2.1.6" % "test",
    "com.twitter.inject"  %% "inject-server"            % "2.1.6" % "test" classifier "tests",
    "com.twitter.inject"  %% "inject-app"               % "2.1.6" % "test",
    "com.twitter.inject"  %% "inject-app"               % "2.1.6" % "test" classifier "tests",
    "com.twitter.inject"  %% "inject-core"              % "2.1.6" % "test",
    "com.twitter.inject"  %% "inject-core"              % "2.1.6" % "test" classifier "tests",
    "com.twitter.inject"  %% "inject-modules"           % "2.1.6" % "test",
    "com.twitter.inject"  %% "inject-modules"           % "2.1.6" % "test" classifier "tests",

    "com.google.inject.extensions" % "guice-testlib"    % "4.0"   % "test"
  ))
  // @formatter:on
}
