package llm.ch01.s2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import llm.core.Tensor

class ElementwiseTest : FunSpec({

    test("plus adds two tensors of the same shape element by element") {
        val a = Tensor(shape = intArrayOf(2, 2), data = doubleArrayOf(1.0, 2.0, 3.0, 4.0))
        val b = Tensor(shape = intArrayOf(2, 2), data = doubleArrayOf(10.0, 20.0, 30.0, 40.0))

        val c = a + b

        c.shape shouldBe intArrayOf(2, 2)
        c.data.toList() shouldBe listOf(11.0, 22.0, 33.0, 44.0)
        // The inputs stay unchanged.
        a.data.toList() shouldBe listOf(1.0, 2.0, 3.0, 4.0)
        b.data.toList() shouldBe listOf(10.0, 20.0, 30.0, 40.0)
    }

    test("plus adds by index, also when an input is a view") {
        val m = Tensor(shape = intArrayOf(2, 3), data = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val t = m.transpose(0, 1) // [[1, 4], [2, 5], [3, 6]], strides [1, 3], data still 1 2 3 4 5 6

        // A view and a contiguous tensor: different strides, same shape.
        (t + Tensor.ones(3, 2)).toString() shouldBe "[[2.0, 5.0], [3.0, 6.0], [4.0, 7.0]]"
        // Two views with the same strides: the sum must still follow the index order of the view.
        (t + t).toString() shouldBe "[[2.0, 8.0], [4.0, 10.0], [6.0, 12.0]]"
    }

    test("plus rejects tensors whose shapes do not fit, and the message names both shapes") {
        val a = Tensor.zeros(2, 3)
        val b = Tensor.zeros(2)

        val error = shouldThrow<IllegalArgumentException> { a + b }

        error.message shouldContain "[2, 3]"
        error.message shouldContain "[2]"
    }
})
