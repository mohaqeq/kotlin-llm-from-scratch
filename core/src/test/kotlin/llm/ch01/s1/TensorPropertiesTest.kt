package llm.ch01.s1

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import llm.core.Tensor

/**
 * Rules that must hold for every shape, checked on many random tensors (kotest-property).
 * The example tests in TensorBasicsTest show single cases; these tests look for the cases nobody wrote down.
 */
class TensorPropertiesTest : FunSpec({

    test("transposing twice with the same dimensions gives back the original view") {
        checkAll(transposeCases) { (t, dim0, dim1) ->
            val back = t.transpose(dim0, dim1).transpose(dim0, dim1)

            back.shape shouldBe t.shape
            back.strides shouldBe t.strides
            back.data shouldBeSameInstanceAs t.data
        }
    }

    test("a transposed view reads each element with the two indices swapped") {
        checkAll(transposeCases) { (t, dim0, dim1) ->
            val tt = t.transpose(dim0, dim1)

            for (index in indicesOf(t.shape)) {
                tt.get(*index.swapped(dim0, dim1)) shouldBe t.get(*index)
            }
        }
    }

    test("reshape to one dimension and back keeps every element in row-major order") {
        checkAll(views) { v ->
            val flat = v.reshape(v.size)
            val back = flat.reshape(*v.shape)

            back.shape shouldBe v.shape
            indicesOf(v.shape).forEachIndexed { k, index ->
                flat[k] shouldBe v.get(*index)
                back.get(*index) shouldBe v.get(*index)
            }
        }
    }

    test("contiguous keeps every value and gives row-major strides") {
        checkAll(views) { v ->
            val c = v.contiguous()

            c.isContiguous shouldBe true
            c.shape shouldBe v.shape
            c.strides shouldBe Tensor.contiguousStrides(v.shape)
            for (index in indicesOf(v.shape)) {
                c.get(*index) shouldBe v.get(*index)
            }
        }
    }
})

/** A tensor with a random shape (minRank to 4 dimensions, each of length 0 to 4) and the values 0, 1, 2, ... */
private fun tensors(minRank: Int): Arb<Tensor> = arbitrary {
    val shape = IntArray(Arb.int(minRank..4).bind())
    for (k in shape.indices) shape[k] = Arb.int(0..4).bind()
    val size = shape.fold(1) { acc, d -> acc * d }
    Tensor(shape, DoubleArray(size) { it.toDouble() })
}

/** A tensor with at least one dimension, and two dimension numbers of it (they may be equal). */
private val transposeCases: Arb<Triple<Tensor, Int, Int>> = arbitrary {
    val t = tensors(minRank = 1).bind()
    Triple(t, Arb.int(t.shape.indices).bind(), Arb.int(t.shape.indices).bind())
}

/** A random tensor; with 2 or more dimensions it is a transposed view, so it is often not contiguous. */
private val views: Arb<Tensor> = arbitrary {
    val t = tensors(minRank = 0).bind()
    if (t.shape.size < 2) t else t.transpose(Arb.int(t.shape.indices).bind(), Arb.int(t.shape.indices).bind())
}

/** Every index of [shape], in row-major order. A scalar has one index (empty); a shape with a 0 has none. */
private fun indicesOf(shape: IntArray): Sequence<IntArray> = sequence {
    if (shape.any { it == 0 }) return@sequence
    val index = IntArray(shape.size)
    while (true) {
        yield(index.copyOf())
        var k = shape.size - 1
        while (k >= 0) {
            index[k]++
            if (index[k] < shape[k]) break
            index[k] = 0
            k--
        }
        if (k < 0) return@sequence
    }
}

private fun IntArray.swapped(i: Int, j: Int): IntArray = copyOf().also {
    it[i] = this[j]
    it[j] = this[i]
}
