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

package com.full360.prometheus.annotation

import scala.annotation.{ StaticAnnotation, compileTimeOnly }
import scala.reflect.api.Trees
import scala.reflect.macros.blackbox

@compileTimeOnly("Enable macro paradise to expand macro annotations")
final class Counter(name: String) extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro Counter.impl
}

object Counter {

  def impl(c: blackbox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def eval(tree: Trees#Tree) = tree match {
      case Literal(Constant(value: String)) ⇒ value
      case _                                ⇒ c.abort(c.enclosingPosition, "Annotation @Benchmark with unexpected annotation type")
    }

    val name = c.prefix.tree match {
      case q"new Counter($name)" ⇒ eval(name)
      case _                     ⇒ c.abort(c.enclosingPosition, "Annotation @Benchmark with unexpected annotation pattern")
    }

    println(s"Name is: $name")

    val result = {
      annottees.map(_.tree).toList match {
        case q"$mods def $methodName[..$tpes](...$args): $returnType = { ..$body }" :: Nil ⇒
          q"""$mods def $methodName[..$tpes](...$args): $returnType = {
                com.full360.prometheus.Prometheus.counter("name","help").inc()
                println("OMG")
                val result = {..$body}
                result
              }"""
        case _                                                                             ⇒
          c.abort(c.enclosingPosition, "Annotation @Benchmark can be used only with methods")
      }
    }
    c.Expr[Any](result)
  }
}