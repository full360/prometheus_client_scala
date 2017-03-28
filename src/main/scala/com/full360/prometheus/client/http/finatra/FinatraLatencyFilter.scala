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

package com.full360.prometheus.client.http.finatra

import com.full360.prometheus.client.http.HttpLatency
import com.full360.prometheus.client.util.Timer

import com.twitter.finagle.http.{ Request, Response }
import com.twitter.finagle.{ Service, SimpleFilter }
import com.twitter.util.Future

class FinatraLatencyFilter extends SimpleFilter[Request, Response] with HttpLatency {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val timer = new Timer

    service(request)
      .onSuccess(response ⇒ register(timer, request, Some(response)))
      .onFailure(_ ⇒ register(timer, request, None))
  }

  def register(timer: Timer, request: Request, response: Option[Response] = None) = super.register(
    timer.stop,
    request.method.toString().toLowerCase,
    request.host.getOrElse("unknown"),
    request.uri,
    response.map(_.getStatusCode()).getOrElse(500)
  )
}
