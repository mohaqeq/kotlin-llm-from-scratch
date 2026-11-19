package llm.ch01.s1

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
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

    test("an element is read through the strides") {
        // 1 2 3
        // 4 5 6
        val m = Tensor(shape = intArrayOf(2, 3), data = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        m[0, 0] shouldBe 1.0
        m[0, 2] shouldBe 3.0
        m[1, 0] shouldBe 4.0
        m[1, 2] shouldBe 6.0

        // data = 0, 1, ..., 23; element (i, j, k) is at 12*i + 4*j + k
        val t = Tensor(shape = intArrayOf(2, 3, 4), data = DoubleArray(24) { it.toDouble() })
        t[1, 0, 2] shouldBe 14.0
        t[1, 2, 3] shouldBe 23.0
    }

    test("a wrong index is rejected, not read from another position") {
        val m = Tensor(shape = intArrayOf(2, 3), data = DoubleArray(6))

        // Without a check, [0, 3] is position 3, which is the element [1, 0].
        val error = shouldThrow<IndexOutOfBoundsException> { m[0, 3] }
        error.message shouldContain "[0, 3]"
        error.message shouldContain "[2, 3]"
        shouldThrow<IndexOutOfBoundsException> { m[-1, 0] }

        // The number of indices must be the number of dimensions.
        shouldThrow<IllegalArgumentException> { m[1] }
        shouldThrow<IllegalArgumentException> { m[0, 0, 0] }
    }

    test("an element is written through the strides") {
        val m = Tensor(shape = intArrayOf(2, 3), data = DoubleArray(6))

        m[1, 2] = 9.0

        m[1, 2] shouldBe 9.0
        m.data[5] shouldBe 9.0 // position 1*3 + 2*1
        m.data.count { it != 0.0 } shouldBe 1

        // A wrong index must not write to another position.
        shouldThrow<IndexOutOfBoundsException> { m[0, 3] = 1.0 }
        shouldThrow<IllegalArgumentException> { m[1] = 1.0 }
        m.data.count { it != 0.0 } shouldBe 1
    }

    test("zeros and ones make a tensor of the shape, filled with 0.0 or 1.0") {
        val z = Tensor.zeros(2, 3)
        z.shape shouldBe intArrayOf(2, 3)
        z.data.toList() shouldBe List(6) { 0.0 }

        val o = Tensor.ones(4)
        o.shape shouldBe intArrayOf(4)
        o.data.toList() shouldBe List(4) { 1.0 }

        // No dimensions: a scalar with one value.
        Tensor.ones().data.toList() shouldBe listOf(1.0)
    }

    test("reshape makes a view with a new shape that shares the data") {
        // 1 2 3        1 2
        // 4 5 6   ->   3 4
        //              5 6
        val t = Tensor(shape = intArrayOf(2, 3), data = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val r = t.reshape(3, 2)

        r.shape shouldBe intArrayOf(3, 2)
        r.strides shouldBe intArrayOf(2, 1)
        r[1, 0] shouldBe 3.0
        r[2, 1] shouldBe 6.0

        // A view shares the data: no copy, and a write through the view changes the original.
        r.data shouldBeSameInstanceAs t.data
        r[0, 1] = 20.0
        t[0, 1] shouldBe 20.0

        // The number of elements must stay the same.
        shouldThrow<IllegalArgumentException> { t.reshape(4, 2) }
    }

    test("transpose swaps two dimensions by swapping their strides, without a copy") {
        // 1 2 3        1 4
        // 4 5 6   ->   2 5
        //              3 6
        val t = Tensor(shape = intArrayOf(2, 3), data = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tt = t.transpose(0, 1)

        tt.shape shouldBe intArrayOf(3, 2)
        tt.strides shouldBe intArrayOf(1, 3)
        tt.data shouldBeSameInstanceAs t.data
        for (i in 0 until 2) {
            for (j in 0 until 3) {
                tt[j, i] shouldBe t[i, j]
            }
        }

        // The original keeps its shape and strides.
        t.shape shouldBe intArrayOf(2, 3)
        t.strides shouldBe intArrayOf(3, 1)

        // Any two dimensions: [2, 3, 4] with strides [12, 4, 1] -> swap 0 and 2.
        val u = Tensor.zeros(2, 3, 4).transpose(0, 2)
        u.shape shouldBe intArrayOf(4, 3, 2)
        u.strides shouldBe intArrayOf(1, 4, 12)
    }

    test("transpose rejects a dimension number that the tensor does not have") {
        val m = Tensor.zeros(2, 3)
        shouldThrow<IllegalArgumentException> { m.transpose(0, 2) }
        shouldThrow<IllegalArgumentException> { m.transpose(-1, 0) }
    }

    test("contiguous copies a view into row-major order; a contiguous tensor returns itself") {
        val t = Tensor(shape = intArrayOf(2, 3), data = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        t.isContiguous shouldBe true
        t.contiguous() shouldBeSameInstanceAs t

        // The transpose reads row by row as 1 4 / 2 5 / 3 6, but its data is still 1 2 3 4 5 6.
        val tt = t.transpose(0, 1)
        tt.isContiguous shouldBe false

        val c = tt.contiguous()
        c.isContiguous shouldBe true
        c.shape shouldBe intArrayOf(3, 2)
        c.strides shouldBe intArrayOf(2, 1)
        c.data.toList() shouldBe listOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0)
        c.data shouldNotBeSameInstanceAs t.data
    }

    test("reshape of a non-contiguous view keeps the row-major order of the view") {
        // The transpose reads row by row as 1 4 2 5 3 6, so its reshape to [6] must too.
        val t = Tensor(shape = intArrayOf(2, 3), data = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val flat = t.transpose(0, 1).reshape(6)

        flat.shape shouldBe intArrayOf(6)
        (0 until 6).map { flat[it] } shouldBe listOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0)

        // A contiguous tensor still reshapes without a copy.
        t.reshape(3, 2).data shouldBeSameInstanceAs t.data
    }
})
