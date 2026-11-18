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

    test("a negative dimension is rejected") {
        // The product is 6 and matches the data, but no dimension can be negative.
        val error = shouldThrow<IllegalArgumentException> {
            Tensor(shape = intArrayOf(-2, -3), data = DoubleArray(6))
        }
        error.message shouldContain "[-2, -3]"
    }

    test("a shape whose size overflows Int is rejected") {
        // 65536 * 65536 = 2^32 wraps to 0 in Int, so empty data would pass a plain length check.
        shouldThrow<ArithmeticException> {
            Tensor(shape = intArrayOf(65536, 65536), data = DoubleArray(0))
        }
    }

    test("row-major strides: the last dimension moves fastest") {
        Tensor(shape = intArrayOf(2, 3, 4), data = DoubleArray(24)).strides shouldBe intArrayOf(12, 4, 1)
        Tensor(shape = intArrayOf(5), data = DoubleArray(5)).strides shouldBe intArrayOf(1)
        Tensor(shape = intArrayOf(), data = doubleArrayOf(7.0)).strides shouldBe intArrayOf()
    }
})
