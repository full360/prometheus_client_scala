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
  namespace: String = "",
  buckets: Seq[Double] = Seq(.005, .01, .025, .05, .075, .1, .25, .5, .75, 1.0, 2.5, 5.0, 7.5, 10.0)
)

object Metric {

  private val registry = new CollectorRegistry(true)

  private val gauges = TrieMap.empty[String, Gauge]
  private val counters = TrieMap.empty[String, Counter]
  private val summaries = TrieMap.empty[String, Summary]
  private val histograms = TrieMap.empty[String, Histogram]

  def gauge(metric: Metric) =
    gauges.getOrElseUpdate(metric.name, Gauge.build()
      .name(metric.name)
      .help(metric.help)
      .namespace(metric.namespace)
      .labelNames(metric.labels.map({ case (key, _) => key }).toSeq: _*)
      .register(registry))

  def counter(metric: Metric) =
    counters.getOrElseUpdate(metric.name, Counter.build()
      .name(metric.name)
      .help(metric.help)
      .namespace(metric.namespace)
      .labelNames(metric.labels.map({ case (key, _) => key }).toSeq: _*)
      .register(registry))

  def summary(metric: Metric) =
    summaries.getOrElseUpdate(metric.name, Summary.build()
      .name(metric.name)
      .help(metric.help)
      .namespace(metric.namespace)
      .labelNames(metric.labels.map({ case (key, _) => key }).toSeq: _*)
      .quantile(0.50, 0.05)
      .quantile(0.90, 0.01)
      .quantile(0.99, 0.01)
      .register(registry))

  def histogram(metric: Metric) =
    histograms.getOrElseUpdate(metric.name, Histogram.build()
      .name(metric.name)
      .help(metric.help)
      .namespace(metric.namespace)
      .labelNames(metric.labels.map({ case (key, _) => key }).toSeq: _*)
      .buckets(metric.buckets: _*)
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
