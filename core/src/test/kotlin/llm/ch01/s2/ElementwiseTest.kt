package llm.ch01.s2

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
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
})
