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

package com.full360.prometheus.client.metric.telemetry

import com.full360.prometheus.client.metric.Metric

import scala.collection.concurrent.TrieMap

import io.prometheus.client.Summary

trait Latency extends Metric {

  def register(duration: Long, labels: String*) = {
    if (this.labels.length != labels.length) {
      throw new RuntimeException("Wrong number of labels to register")
    } else {
      getMetric
        .labels(labels: _*)
        .observe(duration.toDouble)
    }
  }

  def getMetric = Latency.latencies
    .getOrElseUpdate(cacheKey, Summary.build()
      .namespace(namespace)
      .name(name)
      .help(help)
      .labelNames(labels: _*)
      .quantile(0.50, 0.05)
      .quantile(0.90, 0.01)
      .quantile(0.99, 0.01)
      .register(registry))
}

object Latency {

  val latencies = TrieMap.empty[String, Summary]
}
