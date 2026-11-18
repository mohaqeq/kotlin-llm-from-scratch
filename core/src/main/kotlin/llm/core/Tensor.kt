package llm.core

/**
 * An n-dimensional array of Double values.
 *
 * - [shape] gives the length of each dimension, for example [2, 3] for a 2x3 matrix.
 * - [data] holds the values in one flat array.
 */
class Tensor(val shape: IntArray, val data: DoubleArray) {

    private var _size: Int = -1

    init {
        // <exercise ch01-s1> Reject a negative dimension, then data whose length is not the size of the shape.
        require(shape.all { it >= 0 }) { "Shape must be non-negative but was ${shape.contentToString()}" }
        require(size == data.size) { "Shape ${shape.contentToString()} needs $size values, but data has ${data.size}" }
        // </exercise>
    }

    /** Number of elements: the product of all dimensions. */
    val size: Int
        get() {
            // <exercise ch01-s1> Return the product of all dimensions of the shape.
            return if (_size > -1) {
                _size
            } else {
                _size = shape.fold(1) { acc, d -> acc * d }
                _size
            }
            // </exercise>
        }
}
