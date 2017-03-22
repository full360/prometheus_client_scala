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

import scalariform.formatter.preferences.{ AlignParameters, AlignSingleLineCaseStatements, DoubleIndentClassDeclaration, FormattingPreferences, RewriteArrowSymbols }

import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys._

object Settings {

  private lazy val base = Seq(
    name := "prometheus-client-scala",
    organization := "com.full360",
    version := "0.1",
    resolvers := Resolvers(),
    libraryDependencies ++= Dependencies(),
    scalaVersion := "2.11.8",
    scalacOptions := Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:_",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-target:jvm-1.8",
      "-unchecked",
      "-Xfatal-warnings",
      "-Xfuture",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused-import"
    ),
    assemblyJarName in assembly := "prometheus-client-scala.jar",
    shellPrompt := { s => s"${Project.extract(s).currentProject.id} > " }
  )

  private lazy val format = {

    val preferences = FormattingPreferences()
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)

    SbtScalariform.scalariformSettings ++ Seq(
      ScalariformKeys.preferences in Compile := preferences,
      ScalariformKeys.preferences in Test := preferences
    )
  }

  def apply() = base ++ format
}
