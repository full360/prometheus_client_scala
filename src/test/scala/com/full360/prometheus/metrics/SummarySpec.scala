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

package com.full360.prometheus.metrics

import com.full360.prometheus.{ BaseSpec, Metric }

import java.util.concurrent.TimeUnit

import org.hamcrest.MatcherAssert._
import org.hamcrest.Matchers._
import org.scalactic.TolerantNumerics

class SummarySpec extends BaseSpec {

  val name = "name"
  val help = "help"

  val time = 10L
  val timeError = 15.0

  implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(timeError)

  @Summary(Metric(name, help, Map("method" -> "foo")))
  def foo() = TimeUnit.MILLISECONDS.sleep(time)

  @Summary(Metric(name, help, Map("method" -> "bar")))
  def bar() = TimeUnit.MILLISECONDS.sleep(time)

  @Counter(Metric(name, help, Map("method" -> "baz")))
  def baz(a: Int, b: Int): Int = a + b

  "Summary metric" should provide {
    "a summary annotation" which {
      "tracks the time a piece code consumes" in {
        assertThat(Metric.get(), is(""))

        foo()

        val array = Metric.get().replace('\n', ' ').split(' ')

        assert(array(9).toDouble === time.toDouble)
        assert(array(11).toDouble === time.toDouble)
        assert(array(13).toDouble === time.toDouble)
        assert(array(17).toDouble === time.toDouble)

        assertThat(Metric.get(), is(s"""# HELP $name $help\n# TYPE $name summary\n$name{method="foo",quantile="0.5",} ${array(9)}\n$name{method="foo",quantile="0.9",} ${array(11)}\n$name{method="foo",quantile="0.99",} ${array(13)}\n${name}_count{method="foo",} 1.0\n${name}_sum{method="foo",} ${array(17)}\n"""))
      }
      "does not affect parameters and result of the method" in {
        assertThat(baz(4, 6), is(10))
      }
    }
  }
}
