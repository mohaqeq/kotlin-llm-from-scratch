package llm.ch01.s1

import llm.core.Tensor
import llm.core.runSection

fun main() = runSection("ch01-s1", "Tensor basics") {
    val t = Tensor(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
    println("shape = ${t.shape.contentToString()}, size = ${t.size}")
}
