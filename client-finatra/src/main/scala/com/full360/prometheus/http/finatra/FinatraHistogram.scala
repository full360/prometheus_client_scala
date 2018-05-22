/*
 * Copyright © 2018 Full 360 Inc
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

package com.full360.prometheus.http.finatra

import com.full360.prometheus.Prometheus
import com.full360.prometheus.http.HttpHistogram

import scala.concurrent.duration
import scala.concurrent.duration.FiniteDuration

import com.twitter.finagle.http.{ Request, Response }
import com.twitter.finagle.{ Service, SimpleFilter }

class FinatraHistogram extends SimpleFilter[Request, Response] with HttpHistogram with Finatra {

  override def apply(request: Request, service: Service[Request, Response]) = {

    val startTime = System.nanoTime()

    def observe[A](result: A) = {

      val endTime = System.nanoTime()
      val elapsedTime = new FiniteDuration(endTime - startTime, duration.NANOSECONDS)

      val (method, path, _) = extract(request, None)

      Prometheus
        .histogram(histogramName, histogramHelp, histogramNamespace, histogramBuckets, histogramLabels: _*)
        .labels(method, path)
        .observe(elapsedTime.toUnit(histogramTimeUnit))
    }

    service(request)
      .onSuccess(observe)
      .onFailure(observe)
  }
}
