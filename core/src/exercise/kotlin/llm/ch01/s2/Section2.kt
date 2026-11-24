package llm.ch01.s2

import llm.core.Tensor
import llm.core.runSection

/** Demo of ch01-s2: element-wise operations and broadcasting. */
fun main() = runSection("ch01-s2", "Element-wise operations and broadcasting") {
    val a = Tensor(intArrayOf(2, 2), doubleArrayOf(1.0, 2.0, 3.0, 4.0))
    val b = Tensor(intArrayOf(2, 2), doubleArrayOf(10.0, 20.0, 30.0, 40.0))
    println("a     = $a")
    println("b     = $b")
    println("a + b = ${a + b}")
}
