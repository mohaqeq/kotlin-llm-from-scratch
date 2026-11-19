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
        // <exercise ch01-s1> Return the product of all dimensions of the shape.
        sizeOf(shape)
        // </exercise>
    }

    init {
        // <exercise ch01-s1> Reject a negative dimension, then data whose length is not the size of the shape.
        require(shape.all { it >= 0 }) { "Shape must be non-negative but was ${shape.contentToString()}" }
        require(size == data.size) { "Shape ${shape.contentToString()} needs $size values, but data has ${data.size}" }
        // </exercise>
    }


    /** Reads one element: `t[i, j, k]` is at position `i*strides[0] + j*strides[1] + k*strides[2]` in [data]. */
    operator fun get(vararg index: Int): Double {
        // <exercise ch01-s1> Find the position of the index in data with the strides, and return that value.
        return data[positionOf(index)]
        // </exercise>
    }

    /** Writes one element: `t[i, j] = v` changes the value at the position of [index] in [data]. */
    operator fun set(vararg index: Int, value: Double) {
        // <exercise ch01-s1> Check the index as in get, then write the value at its position in data.
        data[positionOf(index)] = value
        // </exercise>
    }

    /**
     * A view with a new shape and the same [data]: no values are copied, so a write through
     * the view changes this tensor too. The new shape must have the same size.
     */
    fun reshape(vararg newShape: Int): Tensor {
        // <exercise ch01-s1> Return a tensor with the new shape that shares this data.
        return Tensor(newShape, data)
        // </exercise>
    }

    /**
     * A view with dimensions [dim0] and [dim1] swapped. It swaps the two shape entries and the two
     * strides; the data is not copied or moved. For a matrix, `transpose(0, 1)` is the transpose.
     */
    fun transpose(dim0: Int, dim1: Int): Tensor {
        // <exercise ch01-s1> Swap the two dimensions in a copy of the shape and of the strides; share the data.
        val newShape = shape.clone().apply { this[dim0] = this[dim1].also { this[dim1] = this[dim0] } }
        val newStrides = strides.clone().apply { this[dim0] = this[dim1].also { this[dim1] = this[dim0] } }
        return Tensor(newShape, data, newStrides)
        // </exercise>
    }

    // <solution-only ch01-s1>
    /** Checks [index] against the shape and returns its position in [data]. */
    private fun positionOf(index: IntArray): Int {
        require(index.size == shape.size) {
            "Index ${index.contentToString()} has ${index.size} values, but shape ${shape.contentToString()} has ${shape.size} dimensions"
        }
        var position = 0
        for (k in index.indices) {
            val i = index[k]
            if (i !in 0 until shape[k]) {
                throw IndexOutOfBoundsException("Index ${index.contentToString()} is out of bounds for shape ${shape.contentToString()}")
            }
            position += i * strides[k]
        }
        return position
    }
    // </solution-only>

    companion object {
        /**
         * Row-major strides for [shape]: `strides[k]` is how many positions in data one step in
         * dimension k moves. The last dimension has stride 1. For shape [2, 3, 4]: [12, 4, 1].
         */
        fun contiguousStrides(shape: IntArray): IntArray {
            // <exercise ch01-s1> Compute row-major strides: the last dimension has stride 1.
            val result = IntArray(shape.size)
            var step = 1
            for (k in shape.indices.reversed()) {
                result[k] = step
                step *= shape[k]
            }
            return result
            // </exercise>
        }

        /** A tensor of the given shape with all values 0.0. `zeros()` is a scalar. */
        fun zeros(vararg shape: Int): Tensor {
            // <exercise ch01-s1> Make a tensor of this shape, filled with 0.0.
            return Tensor(shape, DoubleArray(sizeOf(shape)))
            // </exercise>
        }

        /** A tensor of the given shape with all values 1.0. `ones()` is a scalar. */
        fun ones(vararg shape: Int): Tensor {
            // <exercise ch01-s1> Make a tensor of this shape, filled with 1.0.
            return Tensor(shape, DoubleArray(sizeOf(shape)) { 1.0 })
            // </exercise>
        }

        // <solution-only ch01-s1>
        /** Product of all dimensions; throws ArithmeticException on Int overflow. */
        private fun sizeOf(shape: IntArray): Int = shape.fold(1) { acc, d -> Math.multiplyExact(acc, d) }
        // </solution-only>
    }
}
