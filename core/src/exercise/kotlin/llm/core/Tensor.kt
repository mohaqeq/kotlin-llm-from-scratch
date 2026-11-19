package llm.core

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
        TODO("ch01-s1: Reject a negative dimension, then data whose length is not the size of the shape.")
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
     * A view with a new shape and the same [data]: no values are copied, so a write through
     * the view changes this tensor too. The new shape must have the same size.
     */
    fun reshape(vararg newShape: Int): Tensor {
        TODO("ch01-s1: Return a tensor with the new shape that shares this data.")
    }


    companion object {
        /**
         * Row-major strides for [shape]: `strides[k]` is how many positions in data one step in
         * dimension k moves. The last dimension has stride 1. For shape [2, 3, 4]: [12, 4, 1].
         */
        fun contiguousStrides(shape: IntArray): IntArray {
            TODO("ch01-s1: Compute row-major strides: the last dimension has stride 1.")
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
