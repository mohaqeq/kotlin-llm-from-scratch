package llm.ch01.s1

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import llm.core.Tensor

class TensorBasicsTest : FunSpec({

    test("size is the product of all dimensions") {
        val t = Tensor(shape = intArrayOf(2, 3, 4), data = DoubleArray(24))

        t.size shouldBe 24
    }
})
