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

package com.full360.prometheus.client.metric

import com.full360.prometheus.client.util.Implicits._
import io.prometheus.client.{ Collector, CollectorRegistry }
import io.prometheus.client.hotspot._

trait Metric {

  val namespace: String
  val name: String
  val help: String
  val labels: Seq[String]

  def registry = Metric.registry

  def cacheKey = "%s_%s".format(namespace, name)
}

object Metric {

  val registry = new CollectorRegistry(true)

  override def toString = registry
    .metricFamilySamples()
    .asString

  /** Expose the clear method used when testing */
  def clearRegistry() = {
    registry.clear()
  }

  /** See io.prometheus.client.hotspot.DefaultExports.initialize() */
  def addJVMMetrics() = {
    new StandardExports().register[Collector](registry)
    new MemoryPoolsExports().register[Collector](registry)
    new GarbageCollectorExports().register[Collector](registry)
    new ThreadExports().register[Collector](registry)
    new ClassLoadingExports().register[Collector](registry)
    new VersionInfoExports().register[Collector](registry)
  }

}
