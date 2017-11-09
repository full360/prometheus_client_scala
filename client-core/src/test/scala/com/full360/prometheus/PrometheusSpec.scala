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

package com.full360.prometheus

import com.full360.prometheus.Prometheus._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.MILLISECONDS

import org.scalactic.{ Equality, TolerantNumerics }
import org.scalatest.concurrent.ScalaFutures

class PrometheusSpec extends BaseSpec with ScalaFutures {

  val name: String = "name"
  val help: String = "help"
  val time: Long = 100

  implicit val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(15.0)

  def testGauge(label: String)(expression: => Any) = {
    prometheus(Gauge, name, help, Map("test" -> label)) {
      expression
    }
  }

  def testGaugeFuture(label: String)(expression: => Any) = {
    prometheusFuture(Gauge, name, help, Map("test" -> label)) {
      Future {
        expression
      }
    }
  }

  def testCounter(label: String) = {
    prometheus(Counter, name, help, Map("test" -> label)) {}
  }

  def testCounterFuture(label: String) = {
    prometheusFuture(Counter, name, help, Map("test" -> label)) {
      Future {}
    }
  }

  def testSummary(label: String) = {
    prometheus(Summary, name, help, Map("test" -> label), timeUnit = MILLISECONDS) {
      MILLISECONDS.sleep(time)
    }
  }

  def testSummaryFuture(label: String) = {
    prometheusFuture(Summary, name, help, Map("test" -> label), timeUnit = MILLISECONDS) {
      Future {
        MILLISECONDS.sleep(time)
      }
    }
  }

  def testHistogram(label: String) = {
    prometheus(Histogram, name, help, Map("test" -> label), timeUnit = MILLISECONDS) {
      MILLISECONDS.sleep(time)
    }
  }

  def testHistogramFuture(label: String) = {
    prometheusFuture(Histogram, name, help, Map("test" -> label), timeUnit = MILLISECONDS) {
      Future {
        MILLISECONDS.sleep(time)
      }
    }
  }

  "Prometheus scala wrapper" should provide {
    "a gauge metric" which {
      "increase and decrease by 1" in {

        testGauge("a") {
          registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 1.0\n""")

          testGauge("a") {
            registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 2.0\n""")
          }

          registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 1.0\n""")
        }

        registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 0.0\n""")
      }
      "increase and decrease by 1 with different labels" in {

        testGauge("a") {
          registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 1.0\n""")

          testGauge("b") {
            registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 1.0\n$name{test="b",} 1.0\n""")
          }

          registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 1.0\n$name{test="b",} 0.0\n""")
        }

        registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 0.0\n$name{test="b",} 0.0\n""")
      }
    }
    "a gauge metric for futures" which {
      "increase and decrease by 1" in {

        val futureA = testGaugeFuture("a") {
          registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 1.0\n""")

          val futureB = testGaugeFuture("a") {
            registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 2.0\n""")
          }

          whenReady(futureB) { _ =>
            registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 1.0\n""")
          }
        }

        whenReady(futureA) { _ =>
          registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 0.0\n""")
        }
      }
      "increase and decrease by 1 with different labels" in {

        val futureA = testGaugeFuture("a") {
          registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 1.0\n""")

          val futureB = testGaugeFuture("b") {
            registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 1.0\n$name{test="b",} 1.0\n""")
          }

          whenReady(futureB) { _ =>
            registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 1.0\n$name{test="b",} 0.0\n""")
          }
        }

        whenReady(futureA) { _ =>
          registryShouldBe(s"""# HELP $name $help\n# TYPE $name gauge\n$name{test="a",} 0.0\n$name{test="b",} 0.0\n""")
        }
      }
    }
    "a counter metric" which {
      "increase the counter by 1" in {

        testCounter("a")
        registryShouldBe(s"""# HELP $name $help\n# TYPE $name counter\n$name{test="a",} 1.0\n""")

        testCounter("a")
        registryShouldBe(s"""# HELP $name $help\n# TYPE $name counter\n$name{test="a",} 2.0\n""")
      }
      "increase the counter by 1 with different labels" in {

        testCounter("a")
        registryShouldBe(s"""# HELP $name $help\n# TYPE $name counter\n$name{test="a",} 1.0\n""")

        testCounter("b")
        registryShouldBe(s"""# HELP $name $help\n# TYPE $name counter\n$name{test="a",} 1.0\n$name{test="b",} 1.0\n""")
      }
    }
    "a counter metric for futures" which {
      "increase the counter by 1" in {

        whenReady(testCounterFuture("a")) { _ =>
          registryShouldBe(s"""# HELP $name $help\n# TYPE $name counter\n$name{test="a",} 1.0\n""")

          whenReady(testCounterFuture("a")) { _ =>
            registryShouldBe(s"""# HELP $name $help\n# TYPE $name counter\n$name{test="a",} 2.0\n""")
          }
        }
      }
      "increase the counter by 1 with different labels" in {

        whenReady(testCounterFuture("a")) { _ =>
          registryShouldBe(s"""# HELP $name $help\n# TYPE $name counter\n$name{test="a",} 1.0\n""")

          whenReady(testCounterFuture("b")) { _ =>
            registryShouldBe(s"""# HELP $name $help\n# TYPE $name counter\n$name{test="a",} 1.0\n$name{test="b",} 1.0\n""")
          }
        }
      }
    }
    "a summary metric" which {
      "tracks the time a piece code consumes" in {

        testSummary("a")

        val array = Prometheus.getRegistry.replace('\n', ' ').split(' ')

        assert(array(9).toDouble === time.toDouble)
        assert(array(11).toDouble === time.toDouble)
        assert(array(13).toDouble === time.toDouble)
        assert(array(17).toDouble === time.toDouble)

        registryShouldBe(s"""# HELP $name $help\n# TYPE $name summary\n$name{test="a",quantile="0.5",} ${array(9)}\n$name{test="a",quantile="0.9",} ${array(11)}\n$name{test="a",quantile="0.99",} ${array(13)}\n${name}_count{test="a",} ${array(15)}\n${name}_sum{test="a",} ${array(17)}\n""")
      }
    }
    "a summary metric for futures" which {
      "tracks the time a piece code consumes" in {

        whenReady(testSummaryFuture("a")) { _ =>
          val array = Prometheus.getRegistry.replace('\n', ' ').split(' ')

          assert(array(9).toDouble === time.toDouble)
          assert(array(11).toDouble === time.toDouble)
          assert(array(13).toDouble === time.toDouble)
          assert(array(17).toDouble === time.toDouble)

          registryShouldBe(s"""# HELP $name $help\n# TYPE $name summary\n$name{test="a",quantile="0.5",} ${array(9)}\n$name{test="a",quantile="0.9",} ${array(11)}\n$name{test="a",quantile="0.99",} ${array(13)}\n${name}_count{test="a",} ${array(15)}\n${name}_sum{test="a",} ${array(17)}\n""")
        }
      }
    }
    "a histogram metric" which {
      "tracks the time a piece code consumes" in {
        testHistogram("a")

        val array = Prometheus.getRegistry.replace('\n', ' ').split(' ')

        registryShouldBe(s"""# HELP $name $help\n# TYPE $name histogram\n${name}_bucket{test="a",le="0.005",} ${array(9)}\n${name}_bucket{test="a",le="0.01",} ${array(11)}\n${name}_bucket{test="a",le="0.025",} ${array(13)}\n${name}_bucket{test="a",le="0.05",} ${array(15)}\n${name}_bucket{test="a",le="0.075",} ${array(17)}\n${name}_bucket{test="a",le="0.1",} ${array(19)}\n${name}_bucket{test="a",le="0.25",} ${array(21)}\n${name}_bucket{test="a",le="0.5",} ${array(23)}\n${name}_bucket{test="a",le="0.75",} ${array(25)}\n${name}_bucket{test="a",le="1.0",} ${array(27)}\n${name}_bucket{test="a",le="2.5",} ${array(29)}\n${name}_bucket{test="a",le="5.0",} ${array(31)}\n${name}_bucket{test="a",le="7.5",} ${array(33)}\n${name}_bucket{test="a",le="10.0",} ${array(35)}\n${name}_bucket{test="a",le="+Inf",} ${array(37)}\n${name}_count{test="a",} 1.0\n${name}_sum{test="a",} ${array(41)}\n""")
      }
    }
    "a histogram metric for futures" which {
      "tracks the time a piece code consumes" in {
        whenReady(testHistogramFuture("a")) { _ =>

          val array = Prometheus.getRegistry.replace('\n', ' ').split(' ')

          registryShouldBe(s"""# HELP $name $help\n# TYPE $name histogram\n${name}_bucket{test="a",le="0.005",} ${array(9)}\n${name}_bucket{test="a",le="0.01",} ${array(11)}\n${name}_bucket{test="a",le="0.025",} ${array(13)}\n${name}_bucket{test="a",le="0.05",} ${array(15)}\n${name}_bucket{test="a",le="0.075",} ${array(17)}\n${name}_bucket{test="a",le="0.1",} ${array(19)}\n${name}_bucket{test="a",le="0.25",} ${array(21)}\n${name}_bucket{test="a",le="0.5",} ${array(23)}\n${name}_bucket{test="a",le="0.75",} ${array(25)}\n${name}_bucket{test="a",le="1.0",} ${array(27)}\n${name}_bucket{test="a",le="2.5",} ${array(29)}\n${name}_bucket{test="a",le="5.0",} ${array(31)}\n${name}_bucket{test="a",le="7.5",} ${array(33)}\n${name}_bucket{test="a",le="10.0",} ${array(35)}\n${name}_bucket{test="a",le="+Inf",} ${array(37)}\n${name}_count{test="a",} 1.0\n${name}_sum{test="a",} ${array(41)}\n""")
        }
      }
    }
  }
}
