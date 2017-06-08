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

import scala.collection.concurrent.TrieMap

import java.io.StringWriter

import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client.{ CollectorRegistry, Counter, Gauge, Histogram, Summary }

case class Metric(
  name: String,
  help: String,
  labels: Map[String, String] = Map(),
  namespace: String = ""
)

object Metric {

  private val registry = new CollectorRegistry(true)

  private val gauges = TrieMap.empty[String, Gauge]
  private val counters = TrieMap.empty[String, Counter]
  private val summaries = TrieMap.empty[String, Summary]
  private val histograms = TrieMap.empty[String, Histogram]

  def counter(metric: Metric) =
    counters.getOrElseUpdate(metric.name, Counter.build()
      .name(metric.name)
      .help(metric.help)
      .namespace(metric.namespace)
      .labelNames(metric.labels.map({ case (key, _) => key }).toSeq: _*)
      .register(registry))

  def get() = {
    val writer = new StringWriter
    TextFormat.write004(writer, registry.metricFamilySamples())
    writer.toString
  }

  def clear() = {
    gauges.clear()
    counters.clear()
    summaries.clear()
    histograms.clear()
    registry.clear()
  }
}
