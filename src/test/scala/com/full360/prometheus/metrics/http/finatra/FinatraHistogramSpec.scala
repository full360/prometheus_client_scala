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

import com.full360.prometheus.Metric
import com.full360.prometheus.metrics.http.HttpHistogram

import com.twitter.finagle.http.Status.Ok
import com.twitter.finatra.http.routing.HttpRouter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.is
import org.scalactic.TolerantNumerics

class FinatraHistogramSpec extends FinatraBaseSpec with HttpHistogram {

  implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(200.0)

  override def configureHttp(router: HttpRouter) = {
    router
      .filter[FinatraHistogram]
      .add[FinatraMetric]
  }

  "Histogram metric" should provide {
    "an histogram filter for Finatra" which {
      "tracks the time an endpoint consumes" in {
        server.httpGet(
          path      = "/metrics",
          andExpect = Ok,
          withBody  = ""
        )

        val array = Metric.getRegistry.replace('\n', ' ').split(' ')

        assertThat(Metric.getRegistry, is(
          s"""# HELP ${namespace}_$name $help
             |# TYPE ${namespace}_$name histogram
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="0.005",} ${array(15)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="0.01",} ${array(17)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="0.025",} ${array(19)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="0.05",} ${array(21)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="0.075",} ${array(23)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="0.1",} ${array(25)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="0.25",} ${array(27)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="0.5",} ${array(29)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="0.75",} ${array(31)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="1.0",} ${array(33)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="2.5",} ${array(35)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="5.0",} ${array(37)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="7.5",} ${array(39)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="10.0",} ${array(41)}
             |${namespace}_${name}_bucket{method="get",path="/metrics",le="+Inf",} ${array(43)}
             |${namespace}_${name}_count{method="get",path="/metrics",} ${array(45)}
             |${namespace}_${name}_sum{method="get",path="/metrics",} ${array(47)}
             |""".stripMargin
        ))
      }
    }
  }
}
