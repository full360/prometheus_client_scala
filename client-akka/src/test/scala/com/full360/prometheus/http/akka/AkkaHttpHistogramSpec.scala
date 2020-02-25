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

import com.full360.prometheus.{ BaseSpec, Prometheus }

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
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
        Get() ~> route ~> check {
          responseAs[String] shouldBe "foo"

          val array = Prometheus.getRegistry.replace('\n', ' ').split(' ')

          registryShouldBe(
            s"""# HELP ${histogramNamespace}_$histogramName $histogramHelp
               |# TYPE ${histogramNamespace}_$histogramName histogram
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.005",} ${array(16)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.01",} ${array(18)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.025",} ${array(20)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.05",} ${array(22)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.075",} ${array(24)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.1",} ${array(26)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.25",} ${array(28)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.5",} ${array(30)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="0.75",} ${array(32)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="1.0",} ${array(34)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="2.5",} ${array(36)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="5.0",} ${array(38)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="7.5",} ${array(40)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="10.0",} ${array(42)}
               |${histogramNamespace}_${histogramName}_bucket{method="get",path="/",le="+Inf",} ${array(44)}
               |${histogramNamespace}_${histogramName}_count{method="get",path="/",} ${array(46)}
               |${histogramNamespace}_${histogramName}_sum{method="get",path="/",} ${array(48)}
               |""".stripMargin)
        }
      }
    }
  }
}
