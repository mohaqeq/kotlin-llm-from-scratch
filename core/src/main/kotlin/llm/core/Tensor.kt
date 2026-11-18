package llm.core

/**
 * An n-dimensional array of Double values.
 *
 * - [shape] gives the length of each dimension, for example [2, 3] for a 2x3 matrix.
 * - [data] holds the values in one flat array.
 */
class Tensor(val shape: IntArray, val data: DoubleArray) {

    /** Number of elements: the product of all dimensions. */
    val size: Int by lazy {
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
}
