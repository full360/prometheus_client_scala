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
import io.prometheus.client.hotspot.{ ClassLoadingExports, GarbageCollectorExports, MemoryPoolsExports, StandardExports, ThreadExports, VersionInfoExports }
import io.prometheus.client.{ Collector, CollectorRegistry, Counter, Gauge, Histogram, Summary }

//noinspection ScalaUnusedSymbol
trait Registry {

  private val registry = new CollectorRegistry(true)

  private val gauges = TrieMap.empty[String, Gauge]
  private val counters = TrieMap.empty[String, Counter]
  private val summaries = TrieMap.empty[String, Summary]
  private val histograms = TrieMap.empty[String, Histogram]

  def getRegistry: String = {
    val writer = new StringWriter
    TextFormat.write004(writer, registry.metricFamilySamples())
    writer.toString
  }

  def clearRegistry(): Unit = {
    gauges.clear()
    counters.clear()
    summaries.clear()
    histograms.clear()
    registry.clear()
  }

  def addJVM(): Unit = {
    new StandardExports().register[Collector](registry)
    new MemoryPoolsExports().register[Collector](registry)
    new GarbageCollectorExports().register[Collector](registry)
    new ThreadExports().register[Collector](registry)
    new ClassLoadingExports().register[Collector](registry)
    new VersionInfoExports().register[Collector](registry)
  }

  def gauge(name: String, help: String, namespace: String, labels: Map[String, String]): Gauge = synchronized {
    gauges.getOrElseUpdate(s"${namespace}_$name", Gauge.build()
      .name(name)
      .help(help)
      .namespace(namespace)
      .labelNames(labels.toKeySeq: _*)
      .register(registry))
  }

  def counter(name: String, help: String, namespace: String, labels: Map[String, String]): Counter = synchronized {
    counters.getOrElseUpdate(s"${namespace}_$name", Counter.build()
      .name(name)
      .help(help)
      .namespace(namespace)
      .labelNames(labels.toKeySeq: _*)
      .register(registry))
  }

  def summary(name: String, help: String, namespace: String, labels: Map[String, String]): Summary = synchronized {
    summaries.getOrElseUpdate(s"${namespace}_$name", Summary.build()
      .name(name)
      .help(help)
      .namespace(namespace)
      .labelNames(labels.toKeySeq: _*)
      .quantile(0.50, 0.05)
      .quantile(0.90, 0.01)
      .quantile(0.99, 0.01)
      .register(registry))
  }

  def histogram(name: String, help: String, namespace: String, labels: Map[String, String], buckets: Seq[Double]): Histogram = synchronized {
    histograms.getOrElseUpdate(s"${namespace}_$name", Histogram.build()
      .name(name)
      .help(help)
      .namespace(namespace)
      .labelNames(labels.toKeySeq: _*)
      .buckets(buckets: _*)
      .register(registry))
  }
}
