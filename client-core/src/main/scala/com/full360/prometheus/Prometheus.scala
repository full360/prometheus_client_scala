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

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

//noinspection ScalaUnusedSymbol
object Prometheus extends Registry {

  def prometheus[R](
    metric:    Metric,
    name:      String,
    help:      String,
    labels:    Map[String, String] = Map(),
    namespace: String              = "",
    timeUnit:  TimeUnit            = SECONDS,
    buckets:   Seq[Double]         = Seq(.005, .01, .025, .05, .075, .1, .25, .5, .75, 1.0, 2.5, 5.0, 7.5, 10.0)
  )(block: => R): R = {

    metric match {
      case Gauge     => prometheusGauge(name, help, namespace, labels)(block)
      case Counter   => prometheusCounter(name, help, namespace, labels)(block)
      case Summary   => prometheusSummary(name, help, namespace, labels, timeUnit)(block)
      case Histogram => prometheusHistogram(name, help, namespace, labels, timeUnit, buckets)(block)
    }
  }

  private[this] def prometheusGauge[R](name: String, help: String, namespace: String, labels: Map[String, String])(block: => R): R = {
    val metric = gauge(name, help, namespace, labels).labels(labels.toValueSeq: _*)

    metric.inc()

    try {
      block
    } finally {
      metric.dec()
    }
  }

  private[this] def prometheusCounter[R](name: String, help: String, namespace: String, labels: Map[String, String])(block: => R): R = {
    val metric = counter(name, help, namespace, labels).labels(labels.toValueSeq: _*)

    metric.inc()
    block
  }

  private[this] def prometheusSummary[R](name: String, help: String, namespace: String, labels: Map[String, String], timeUnit: TimeUnit)(block: => R): R = {
    val metric = summary(name, help, namespace, labels).labels(labels.toValueSeq: _*)
    val startTime = System.nanoTime()

    try {
      block
    } finally {
      val endTime = System.nanoTime()
      val elapsedTime = new FiniteDuration(endTime - startTime, NANOSECONDS)

      metric.observe(elapsedTime.toUnit(timeUnit))
    }
  }

  private[this] def prometheusHistogram[R](name: String, help: String, namespace: String, labels: Map[String, String], timeUnit: TimeUnit, buckets: Seq[Double])(block: => R): R = {
    val metric = histogram(name, help, namespace, labels, buckets).labels(labels.toValueSeq: _*)
    val startTime = System.nanoTime()

    try {
      block
    } finally {
      val endTime = System.nanoTime()
      val elapsedTime = new FiniteDuration(endTime - startTime, NANOSECONDS)

      metric.observe(elapsedTime.toUnit(timeUnit))
    }
  }

  def prometheusFuture[R](
    metric:    Metric,
    name:      String,
    help:      String,
    labels:    Map[String, String] = Map(),
    namespace: String              = "",
    timeUnit:  TimeUnit            = SECONDS,
    buckets:   Seq[Double]         = Seq(.005, .01, .025, .05, .075, .1, .25, .5, .75, 1.0, 2.5, 5.0, 7.5, 10.0)
  )(block: => Future[R])(implicit ec: ExecutionContext): Future[R] = {

    metric match {
      case Gauge     => prometheusGaugeFuture(name, help, namespace, labels)(block)
      case Counter   => prometheusCounterFuture(name, help, namespace, labels)(block)
      case Summary   => prometheusSummaryFuture(name, help, namespace, labels, timeUnit)(block)
      case Histogram => prometheusHistogramFuture(name, help, namespace, labels, timeUnit, buckets)(block)
    }
  }

  private[this] def prometheusGaugeFuture[R](name: String, help: String, namespace: String, labels: Map[String, String])(block: => Future[R])(implicit ec: ExecutionContext): Future[R] = {
    val metric = gauge(name, help, namespace, labels).labels(labels.toValueSeq: _*)

    metric.inc()

    block.map { f =>
      metric.dec()
      f
    }
  }

  private[this] def prometheusCounterFuture[R](name: String, help: String, namespace: String, labels: Map[String, String])(block: => Future[R])(implicit ec: ExecutionContext): Future[R] = {
    val metric = counter(name, help, namespace, labels).labels(labels.toValueSeq: _*)

    block.map { f =>
      metric.inc()
      f
    }
  }

  private[this] def prometheusSummaryFuture[R](name: String, help: String, namespace: String, labels: Map[String, String], timeUnit: TimeUnit)(block: => Future[R])(implicit ec: ExecutionContext): Future[R] = {
    val metric = summary(name, help, namespace, labels).labels(labels.toValueSeq: _*)
    val startTime = System.nanoTime()

    block.map { f =>
      val endTime = System.nanoTime()
      val elapsedTime = new FiniteDuration(endTime - startTime, NANOSECONDS)

      metric.observe(elapsedTime.toUnit(timeUnit))

      f
    }
  }

  private[this] def prometheusHistogramFuture[R](name: String, help: String, namespace: String, labels: Map[String, String], timeUnit: TimeUnit, buckets: Seq[Double])(block: => Future[R])(implicit ec: ExecutionContext): Future[R] = {
    val metric = histogram(name, help, namespace, labels, buckets).labels(labels.toValueSeq: _*)
    val startTime = System.nanoTime()

    block.map { f =>
      val endTime = System.nanoTime()
      val elapsedTime = new FiniteDuration(endTime - startTime, NANOSECONDS)

      metric.observe(elapsedTime.toUnit(timeUnit))

      f
    }
  }

  sealed trait Metric

  case object Gauge extends Metric

  case object Counter extends Metric

  case object Summary extends Metric

  case object Histogram extends Metric
}
