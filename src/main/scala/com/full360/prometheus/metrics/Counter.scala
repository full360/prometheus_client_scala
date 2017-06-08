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

import com.full360.prometheus.Metric

import scala.annotation.{ StaticAnnotation, compileTimeOnly }
import scala.reflect.macros.blackbox

@compileTimeOnly("Enable macro paradise to expand macro annotations")
final class Counter(metric: Metric) extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro Counter.impl
}

object Counter {

  def impl(c: blackbox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val params = c.prefix.tree match {
      case q"""new Counter(...$params)""" => params
      case _                              => c.abort(c.enclosingPosition, "Annotation @Counter with unexpected parameter pattern")
    }

    val metric = params match {
      case List(List(tree)) => tree
      case _                => c.abort(c.enclosingPosition, "Annotation @Counter with unexpected parameter pattern")
    }

    val result = annottees.map(_.tree).toList match {
      case q"$mods def $methodName[..$types](...$args): $returnType = { ..$body }" :: Nil =>
        q"""$mods def $methodName[..$types](...$args): $returnType = {
                import com.full360.prometheus.Metric

                Metric.counter($metric)
                      .labels($metric.labels.map({case (_, value) => value}).toSeq: _*)
                      .inc()

                $body
              }"""
      case _                                                                              =>
        c.abort(c.enclosingPosition, "Annotation @Counter can be used only with methods")
    }

    c.Expr[Any](result)
  }
}