package llm.ch01.s1

import llm.core.Tensor
import llm.core.runSection

/** Demo of ch01-s1: one tensor, its views, and what they share. */
fun main() = runSection("ch01-s1", "Tensor basics") {
    val t = Tensor(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
    println("t                  = $t")
    println("  shape ${t.shape.contentToString()}, strides ${t.strides.contentToString()}, size ${t.size}")

    val tt = t.transpose(0, 1)
    println("t.transpose(0, 1)  = $tt")
    println(
        "  strides ${tt.strides.contentToString()}, contiguous: ${tt.isContiguous}, same data: ${tt.data === t.data}",
    )

    println("t.reshape(3, 2)    = ${t.reshape(3, 2)}")
    println("tt.reshape(6)      = ${tt.reshape(6)}   (a copy: tt is not contiguous)")

    t[0, 1] = 20.0
    println("after t[0, 1] = 20.0, the view tt is $tt")
}
