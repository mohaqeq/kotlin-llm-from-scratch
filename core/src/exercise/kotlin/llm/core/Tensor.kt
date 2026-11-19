package llm.core

/**
 * An n-dimensional array of Double values.
 *
 * - [shape] gives the length of each dimension, for example [2, 3] for a 2x3 matrix.
 * - [data] holds the values in one flat array.
 */
class Tensor(val shape: IntArray, val data: DoubleArray) {

    /** Number of elements: the product of all dimensions. */
    val size: Int = run {
        TODO("ch01-s1: Return the product of all dimensions of the shape.")
    }

    init {
        TODO("ch01-s1: Reject a negative dimension, then data whose length is not the size of the shape.")
    }

    /**
     * Row-major strides: `strides[k]` is how many positions in [data] one step in dimension k moves.
     * For shape [2, 3, 4] the strides are [12, 4, 1].
     */
    val strides: IntArray = run {
        TODO("ch01-s1: Compute row-major strides: the last dimension has stride 1.")
    }

    /** Reads one element: `t[i, j, k]` is at position `i*strides[0] + j*strides[1] + k*strides[2]` in [data]. */
    operator fun get(vararg index: Int): Double {
        TODO("ch01-s1: Find the position of the index in data with the strides, and return that value.")
    }

    /** Writes one element: `t[i, j] = v` changes the value at the position of [index] in [data]. */
    operator fun set(vararg index: Int, value: Double) {
        TODO("ch01-s1: Check the index as in get, then write the value at its position in data.")
    }


    companion object {
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
