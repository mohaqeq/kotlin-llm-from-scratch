package llm.core

/**
 * An n-dimensional array of Double values.
 *
 * - [shape] gives the length of each dimension, for example [2, 3] for a 2x3 matrix.
 * - [data] holds the values in one flat array.
 */
class Tensor(val shape: IntArray, val data: DoubleArray) {

    /** Number of elements: the product of all dimensions. */
    val size: Int
        get() {
            // <exercise ch01-s1> Return the product of all dimensions of the shape.
            return shape.fold(1) { acc, d -> acc * d }
            // </exercise>
        }
}
