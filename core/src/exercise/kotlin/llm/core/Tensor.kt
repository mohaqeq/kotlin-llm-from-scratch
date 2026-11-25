package llm.core

import kotlin.math.max

/**
 * An n-dimensional array of Double values.
 *
 * - [shape] gives the length of each dimension, for example [2, 3] for a 2x3 matrix.
 * - [data] holds the values in one flat array.
 * - [strides] gives, for each dimension, how many positions in [data] one step moves.
 *   The default is row-major ([contiguousStrides]); views such as transpose use other strides.
 */
class Tensor(
    val shape: IntArray,
    val data: DoubleArray,
    val strides: IntArray = contiguousStrides(shape),
) {

    /** Number of elements: the product of all dimensions. */
    val size: Int = run {
        TODO("ch01-s1: Return the product of all dimensions of the shape.")
    }

    init {
        TODO("ch01-s1: Reject a negative dimension.")
        TODO("ch01-s2: Row-major: data has exactly size values. Other strides: every reachable position is in data.")
    }

    /** Reads one element: `t[i, j, k]` is at position `i*strides[0] + j*strides[1] + k*strides[2]` in [data]. */
    operator fun get(vararg index: Int): Double {
        TODO("ch01-s1: Find the position of the index in data with the strides, and return that value.")
    }

    /** Writes one element: `t[i, j] = v` changes the value at the position of [index] in [data]. */
    operator fun set(vararg index: Int, value: Double) {
        TODO("ch01-s1: Check the index as in get, then write the value at its position in data.")
    }

    /**
     * The same elements, in row-major order, with a new shape of the same size.
     * A contiguous tensor gives a view that shares [data] (a write through it changes this
     * tensor too); any other tensor is first copied with [contiguous].
     */
    fun reshape(vararg newShape: Int): Tensor {
        TODO("ch01-s1: Return a tensor with the new shape over the row-major data of this tensor.")
    }

    /**
     * A view with dimensions [dim0] and [dim1] swapped. It swaps the two shape entries and the two
     * strides; the data is not copied or moved. For a matrix, `transpose(0, 1)` is the transpose.
     */
    fun transpose(dim0: Int, dim1: Int): Tensor {
        TODO("ch01-s1: Swap the two dimensions in a copy of the shape and of the strides; share the data.")
    }

    /** True when the strides are row-major for the shape, so [data] holds the elements in row-major order. */
    val isContiguous: Boolean
        get() {
            TODO("ch01-s1: Compare the strides with the row-major strides of the shape.")
        }

    /**
     * A tensor with the same shape and values whose data is in row-major order. A contiguous
     * tensor returns itself (no copy); any other view copies its elements into a new array.
     */
    fun contiguous(): Tensor {
        TODO("ch01-s1: Return this when contiguous; else copy the elements in row-major index order.")
    }

    /**
     * The values in nested brackets, one level per dimension, in the order of the view:
     * a scalar prints as `7.0`, a 2x2 matrix as `[[1.0, 2.0], [3.0, 4.0]]`.
     */
    override fun toString(): String {
        TODO("ch01-s1: Print one pair of brackets per dimension; read the values through the strides.")
    }

    /** Element-wise sum: a new tensor where each element is `this[index] + other[index]`. */
    operator fun plus(other: Tensor): Tensor {
        TODO("ch01-s2: Add the two tensors element by element into a new tensor.")
    }



    companion object {
        /**
         * Row-major strides for [shape]: `strides[k]` is how many positions in data one step in
         * dimension k moves. The last dimension has stride 1. For shape [2, 3, 4]: [12, 4, 1].
         */
        fun contiguousStrides(shape: IntArray): IntArray {
            TODO("ch01-s1: Compute row-major strides: the last dimension has stride 1.")
        }

        /**
         * The shape that [a] and [b] broadcast to. The shapes are aligned from the right. Two dimensions fit when they
         * are equal or one of them is 1; the result has the larger one. A missing dimension counts as 1.
         * Example: [3, 1] and [1, 4] give [3, 4]. Throws IllegalArgumentException when the shapes do not fit.
         */
        fun broadcastShapes(a: IntArray, b: IntArray): IntArray {
            TODO("ch01-s2: Align the shapes from the right; equal dimensions or a 1 fit, else throw.")
        }

        /** A tensor of the given shape with all values 0.0. `zeros()` is a scalar. */
        fun zeros(vararg shape: Int): Tensor {
            TODO("ch01-s1: Make a tensor of this shape, filled with 0.0.")
        }

        /** A tensor of the given shape with all values 1.0. `ones()` is a scalar. */
        fun ones(vararg shape: Int): Tensor {
            TODO("ch01-s1: Make a tensor of this shape, filled with 1.0.")
        }

    }
}
