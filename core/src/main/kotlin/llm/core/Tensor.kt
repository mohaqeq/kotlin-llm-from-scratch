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
        // <exercise ch01-s1> Return the product of all dimensions of the shape.
        shape.fold(1) { acc, d -> Math.multiplyExact(acc, d) }
        // </exercise>
    }

    init {
        // <exercise ch01-s1> Reject a negative dimension, then data whose length is not the size of the shape.
        require(shape.all { it >= 0 }) { "Shape must be non-negative but was ${shape.contentToString()}" }
        require(size == data.size) { "Shape ${shape.contentToString()} needs $size values, but data has ${data.size}" }
        // </exercise>
    }

    /**
     * Row-major strides: `strides[k]` is how many positions in [data] one step in dimension k moves.
     * For shape [2, 3, 4] the strides are [12, 4, 1].
     */
    val strides: IntArray = run {
        // <exercise ch01-s1> Compute row-major strides: the last dimension has stride 1.
        val result = IntArray(shape.size)
        var step = 1
        for (k in shape.indices.reversed()) {
            result[k] = step
            step *= shape[k]
        }
        result
        // </exercise>
    }

    /** Reads one element: `t[i, j, k]` is at position `i*strides[0] + j*strides[1] + k*strides[2]` in [data]. */
    operator fun get(vararg index: Int): Double {
        // <exercise ch01-s1> Find the position of the index in data with the strides, and return that value.
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
        return data[position]
        // </exercise>
    }

    /** Writes one element: `t[i, j] = v` changes the value at the position of [index] in [data]. */
    operator fun set(vararg index: Int, value: Double) {
        // <exercise ch01-s1> Check the index as in get, then write the value at its position in data.
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
        data[position] = value
        // </exercise>
    }
}
