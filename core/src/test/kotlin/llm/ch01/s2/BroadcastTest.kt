package llm.ch01.s2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import llm.core.Tensor

class BroadcastTest : FunSpec({

    test("broadcastShapes aligns the shapes from the right, and a dimension of 1 stretches") {
        fun shape(vararg d: Int) = d

        // Equal shapes stay the same.
        Tensor.broadcastShapes(shape(2, 3), shape(2, 3)) shouldBe shape(2, 3)
        // A missing dimension counts as 1: a row [3] is used for every row of [2, 3].
        Tensor.broadcastShapes(shape(2, 3), shape(3)) shouldBe shape(2, 3)
        // Each side can stretch: a column [3, 1] and a row [1, 4] give a [3, 4] grid.
        Tensor.broadcastShapes(shape(3, 1), shape(1, 4)) shouldBe shape(3, 4)
        Tensor.broadcastShapes(shape(2, 1, 4), shape(3, 1)) shouldBe shape(2, 3, 4)
        // A scalar fits every shape.
        Tensor.broadcastShapes(shape(), shape(2, 3)) shouldBe shape(2, 3)

        // 3 and 2 are different, and neither is 1.
        val error = shouldThrow<IllegalArgumentException> { Tensor.broadcastShapes(shape(2, 3), shape(2)) }
        error.message shouldContain "[2, 3]"
        error.message shouldContain "[2]"
    }

    test("a dimension of 1 stretches to the other size, also to an empty dimension of 0") {
        fun shape(vararg d: Int) = d

        // 1 stretches to 0, as it stretches to any other size.
        Tensor.broadcastShapes(shape(2, 0), shape(1)) shouldBe shape(2, 0)
        Tensor.broadcastShapes(shape(1, 3), shape(0, 1)) shouldBe shape(0, 3)
        // 0 and 3 are different, and neither is 1.
        shouldThrow<IllegalArgumentException> { Tensor.broadcastShapes(shape(0), shape(3)) }
    }
})
