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

import sbt._

object Dependencies {

  val prometheusClient  = "io.prometheus"      % "simpleclient"         % Versions.prometheus
  val prometheusCommon  = "io.prometheus"      % "simpleclient_common"  % Versions.prometheus
  val prometheusHotspot = "io.prometheus"      % "simpleclient_hotspot" % Versions.prometheus
  val scalatest         = "org.scalatest"     %% "scalatest"            % Versions.scalatest
  val mockito           = "org.mockito"        % "mockito-core"         % Versions.mockito
  val junit             = "junit"              % "junit"                % Versions.junit
  val akkaHttp          = "com.typesafe.akka" %% "akka-http"            % Versions.akka
  val akkaHttpTest      = "com.typesafe.akka" %% "akka-http-testkit"    % Versions.akka
  val finatraHttp       = "com.twitter"       %% "finatra-http"         % Versions.finatra
  val finatraHttpTest   = "com.twitter"       %% "finatra-http"         % Versions.finatra
  val finatraTestCore   = "com.twitter"       %% "inject-core"          % Versions.finatra
  val finatraTestServer = "com.twitter"       %% "inject-server"        % Versions.finatra
  val finatraTestApp    = "com.twitter"       %% "inject-app"           % Versions.finatra
  val finatraTestModules= "com.twitter"       %% "inject-modules"       % Versions.finatra
  val finatraTestGuice  = "com.google.inject.extensions" % "guice-testlib" % Versions.guice

  // @formatter:off
  def compile       (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")

  def provided      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")

  def runtime       (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")

  def container     (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  def testClassifier(deps: ModuleID*): Seq[ModuleID] = test(deps: _*) map(_ classifier "tests")

  def test          (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")

  private[this] object Versions {
    val prometheus  = "0.0.26"
    val akka        = "10.0.9"
    val finatra     = "20.1.0"
    val scalatest   = "3.0.4"
    val mockito     = "1.9.5"
    val junit       = "4.12"
    val guice       = "4.1.0"
  }
  // @formatter:on
}
