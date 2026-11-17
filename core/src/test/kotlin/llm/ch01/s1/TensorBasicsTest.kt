package llm.ch01.s1

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import llm.core.Tensor

class TensorBasicsTest : FunSpec({

    test("size is the product of all dimensions") {
        val t = Tensor(shape = intArrayOf(2, 3, 4), data = DoubleArray(24))

        t.size shouldBe 24
    }

    test("a scalar has an empty shape and size 1") {
        val scalar = Tensor(shape = intArrayOf(), data = doubleArrayOf(42.0))

        scalar.size shouldBe 1
    }

    test("the data length must match the shape") {
        val error = shouldThrow<IllegalArgumentException> {
            Tensor(shape = intArrayOf(2, 3), data = DoubleArray(5))
        }
        error.message shouldContain "[2, 3]"
        error.message shouldContain "5"

        // A scalar holds exactly one value.
        shouldThrow<IllegalArgumentException> {
            Tensor(shape = intArrayOf(), data = DoubleArray(0))
        }

        // A dimension of 0 gives a valid empty tensor.
        shouldNotThrowAny {
            Tensor(shape = intArrayOf(2, 0, 3), data = DoubleArray(0))
        }
    }
})
