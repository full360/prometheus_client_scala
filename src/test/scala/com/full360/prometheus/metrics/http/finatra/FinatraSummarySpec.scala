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
import com.full360.prometheus.metrics.http.HttpSummary

import com.twitter.finagle.http.Status.Ok
import com.twitter.finatra.http.routing.HttpRouter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.is
import org.scalactic.TolerantNumerics

class FinatraSummarySpec extends FinatraBaseSpec with HttpSummary {

  implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(200.0)

  override def configureHttp(router: HttpRouter) = {
    router
      .filter[FinatraSummary]
      .add[FinatraMetric]
  }

  test("Summary metric should provide a summary filter for Finatra which tracks the time an endpoint consumes") {
    server.httpGet(
      path      = "/metrics",
      andExpect = Ok,
      withBody  = ""
    )

    val array = Metric.getRegistry.replace('\n', ' ').split(' ')

    assertThat(Metric.getRegistry, is(
      s"""# HELP ${summaryNamespace}_$summaryName $summaryHelp
         |# TYPE ${summaryNamespace}_$summaryName summary
         |${summaryNamespace}_$summaryName{method="get",code="200",path="/metrics",quantile="0.5",} ${array(16)}
         |${summaryNamespace}_$summaryName{method="get",code="200",path="/metrics",quantile="0.9",} ${array(18)}
         |${summaryNamespace}_$summaryName{method="get",code="200",path="/metrics",quantile="0.99",} ${array(20)}
         |${summaryNamespace}_${summaryName}_count{method="get",code="200",path="/metrics",} ${array(22)}
         |${summaryNamespace}_${summaryName}_sum{method="get",code="200",path="/metrics",} ${array(24)}
         |""".stripMargin
    ))
  }
}
