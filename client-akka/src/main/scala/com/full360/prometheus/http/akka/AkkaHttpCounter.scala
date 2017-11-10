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

package com.full360.prometheus.http.akka

import com.full360.prometheus.Prometheus
import com.full360.prometheus.http.HttpCounter

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives.{ extractRequestContext, handleExceptions, mapResponse }

trait AkkaHttpCounter extends HttpCounter with AkkaHttp {

  def counter: Directive0 = counterPath()

  def counterPath(uri: String = ""): Directive0 =
    extractRequestContext.flatMap { context ⇒
      mapResponse { response ⇒

        val (method, code, path) = extract(uri, context, response)

        Prometheus
          .counter(counterName, counterHelp, counterNamespace, counterLabels)
          .labels(method, code, path)
          .inc()

        response
      }
    } & handleExceptions(exceptionHandler(uri))

  override def onError(uri: String, request: HttpRequest, throwable: Throwable) = {

    val (method, path) = extract(uri, request)

    Prometheus
      .counter(counterName, counterHelp, counterNamespace, counterLabels)
      .labels(method, 500 toString, path)
      .inc()
  }
}
