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

package com.full360.prometheus.metrics.http.akka

import com.full360.prometheus.{ BaseSpec, Metric }

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.is
import org.scalactic.TolerantNumerics

class AkkaHttpHistogramSpec extends BaseSpec with ScalatestRouteTest with AkkaHttpHistogram {

  implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(200.0)

  val route =
    histogram {
      pathSingleSlash {
        get {
          complete("foo")
        }
      }
    }

  "Histogram metric" should provide {
    "an histogram DSL for Akka Http" which {
      "tracks the time an endpoint consumes" in {
        assertThat(Metric.getRegistry, is(""))

        Get() ~> route ~> check {
          assertThat(responseAs[String], is("foo"))

          val array = Metric.getRegistry.replace('\n', ' ').split(' ')

          assertThat(Metric.getRegistry, is(
            s"""# HELP ${histogramNamespace}_$histogramName $histogramHelp
               |# TYPE ${histogramNamespace}_$histogramName histogram
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.005",} ${array(15)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.01",} ${array(17)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.025",} ${array(19)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.05",} ${array(21)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.075",} ${array(23)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.1",} ${array(25)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.25",} ${array(27)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.5",} ${array(29)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.75",} ${array(31)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="1.0",} ${array(33)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="2.5",} ${array(35)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="5.0",} ${array(37)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="7.5",} ${array(39)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="10.0",} ${array(41)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="+Inf",} ${array(43)}
               |${histogramNamespace}_${histogramName}_count{method="get",path="/",} ${array(45)}
               |${histogramNamespace}_${histogramName}_sum{method="get",path="/",} ${array(47)}
               |""".stripMargin
          ))
        }
      }
    }
  }
}
