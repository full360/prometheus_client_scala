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

import sbt.Keys.{publishTo, _}
import sbt.{Credentials, _}

object Publish {

  private lazy val credential = Credentials(
    "Sonatype Nexus Repository Manager",
    sys.env.getOrElse("SONATYPE_REPO", "oss.sonatype.org"),
    sys.env.getOrElse("SONATYPE_USERNAME", ""),
    sys.env.getOrElse("SONATYPE_PASSWORD", "")
  )

  private lazy val pom = {
      <scm>
        <connection>scm:git:github.com/TeletronicsDotAe/prometheus_client_scala.git</connection>
        <developerConnection>scm:git:git@github.com:TeletronicsDotAe/prometheus_client_scala.git</developerConnection>
        <url>https://github.com/TeletronicsDotAe/prometheus_client_scala</url>
      </scm>
      <developers>
        <developer>
          <id>igoticecream</id>
          <name>Pedro Diaz</name>
          <email>pedro.diaz@full360.com</email>
          <organization>Full 360 Inc</organization>
          <organizationUrl>http://www.full360.com</organizationUrl>
        </developer>
        <developer>
          <id>trym-moeller</id>
          <name>Trym Moeller</name>
        </developer>
      </developers>
  }

  def apply() = Seq(
    publishMavenStyle := true,
    credentials += credential,
    pomExtra := pom,

    publishTo := Some("TLT Maven releases" at "http://nexus:8081/nexus/content/repositories/releases"),
    updateOptions := updateOptions.value.withCachedResolution(true)
  )
}
