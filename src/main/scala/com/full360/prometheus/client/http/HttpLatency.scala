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

package com.full360.prometheus.client.http

import com.full360.prometheus.client.metric.namespace.Http
import com.full360.prometheus.client.metric.telemetry.Latency

trait HttpLatency extends Http with Latency {

  override val name = "request_duration_seconds"
  override val help = "A Summary for response latency"
  override val labels = Seq(
    "method",
    "host",
    "uri",
    "response"
  )

  def register(duration: Double, method: String, host: String, uri: String, response: Int) = {
    super.register(duration, method, host, uri, response.toString)
  }
}
