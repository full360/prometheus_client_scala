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

package com.full360.prometheus.metrics.http.finatra

import com.twitter.finagle.http.Request
import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.finatra.http.{ Controller, HttpServer }
import com.twitter.inject.server.FeatureTest

class FinatraDummySpec extends FeatureTest {

  lazy val controller = new Controller {
    get("/") { _: Request => "foo" }
  }
  lazy val http = new HttpServer {

    override val disableAdminHttpServer = true

    override protected def configureHttp(router: HttpRouter) = {
      router.add(controller)
    }
  }
  override val server = new EmbeddedHttpServer(
    twitterServer      = http,
    verbose            = false,
    disableTestLogging = true
  )

  "Server" should {
    "Say hi" in {
      server.httpGet(
        path      = "/",
        andExpect = Ok,
        withBody  = "foo"
      )
    }
  }
}
