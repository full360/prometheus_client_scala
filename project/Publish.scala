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

import com.typesafe.sbt.SbtPgp.autoImportImpl._
import sbt.Credentials
import sbt.Keys._

object Publish {

  private lazy val credentials_ = Credentials(
      "Sonatype Nexus Repository Manager",
      "oss.sonatype.org",
      sys.props("sonatype.username"),
      sys.props("sonatype.password")
    )

  private lazy val pomExtra_ = {
    <url>https://github.com/full360/prometheus_client_scala</url>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>http://www.opensource.org/licenses/mit-license.php</url>
        </license>
      </licenses>
      <scm>
        <connection>scm:git:github.com/full360/prometheus_client_scala.git</connection>
        <developerConnection>scm:git:git@github.com:full360/prometheus_client_scala.git</developerConnection>
        <url>github.com/full360/prometheus_client_scala.git</url>
      </scm>
      <developers>
        <developer>
          <id>igoticecream</id>
          <name>Pedro Diaz</name>
          <email>pedro.diaz@full360.com</email>
          <organization>Full 360 Inc</organization>
          <organizationUrl>http://www.full360.com</organizationUrl>
        </developer>
      </developers>
  }

  def apply() = Seq(
    credentials += credentials_,
    pomExtra := pomExtra_,
    pgpPassphrase := Option(sys.props("signing.passphrase")).map(_.toArray),
    usePgpKeyHex(sys.props("signing.keyid"))
  )
}