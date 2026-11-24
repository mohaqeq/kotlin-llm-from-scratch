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
        sizeOf(shape)
    }

    init {
        require(shape.all { it >= 0 }) { "Shape must be non-negative but was ${shape.contentToString()}" }
        require(size == data.size) { "Shape ${shape.contentToString()} needs $size values, but data has ${data.size}" }
    }

    /** Reads one element: `t[i, j, k]` is at position `i*strides[0] + j*strides[1] + k*strides[2]` in [data]. */
    operator fun get(vararg index: Int): Double {
        return data[positionOf(index)]
    }

    /** Writes one element: `t[i, j] = v` changes the value at the position of [index] in [data]. */
    operator fun set(vararg index: Int, value: Double) {
        data[positionOf(index)] = value
    }

    /**
     * The same elements, in row-major order, with a new shape of the same size.
     * A contiguous tensor gives a view that shares [data] (a write through it changes this
     * tensor too); any other tensor is first copied with [contiguous].
     */
    fun reshape(vararg newShape: Int): Tensor {
        return Tensor(newShape, contiguous().data)
    }

    /**
     * A view with dimensions [dim0] and [dim1] swapped. It swaps the two shape entries and the two
     * strides; the data is not copied or moved. For a matrix, `transpose(0, 1)` is the transpose.
     */
    fun transpose(dim0: Int, dim1: Int): Tensor {
        require(dim0 in shape.indices && dim1 in shape.indices) {
            "Dimension $dim0 or $dim1 is out of range ${shape.contentToString()}"
        }

        return Tensor(shape.swapped(dim0, dim1), data, strides.swapped(dim0, dim1))
    }

    /** True when the strides are row-major for the shape, so [data] holds the elements in row-major order. */
    val isContiguous: Boolean
        get() {
            val contiguousStrides = contiguousStrides(shape)
            return strides.contentEquals(contiguousStrides)
        }

    /**
     * A tensor with the same shape and values whose data is in row-major order. A contiguous
     * tensor returns itself (no copy); any other view copies its elements into a new array.
     */
    fun contiguous(): Tensor {
        return if (isContiguous) {
            this
        } else {
            val contiguousStrides = contiguousStrides(shape)
            val out = DoubleArray(size)
            for (idx in 0..<size) {
                val index = IntArray(shape.size)
                var remainder = idx
                contiguousStrides.forEachIndexed { k, stride ->
                    index[k] = remainder / stride
                    remainder %= stride
                }
                out[idx] = data[offsetOf(index)]
            }
            Tensor(shape, out, contiguousStrides)
        }
    }

    /**
     * The values in nested brackets, one level per dimension, in the order of the view:
     * a scalar prints as `7.0`, a 2x2 matrix as `[[1.0, 2.0], [3.0, 4.0]]`.
     */
    override fun toString(): String {
        return render(dim = 0, position = 0)
    }

    /** Element-wise sum: a new tensor where each element is `this[index] + other[index]`. */
    operator fun plus(other: Tensor): Tensor {
        TODO("ch01-s2: Add the two tensors element by element into a new tensor.")
    }


    /**
     * Text of the part of the tensor that starts at [position] in [data], from dimension [dim] inward.
     * After the last dimension, the part is one value.
     */
    private fun render(dim: Int, position: Int): String {
        if (dim == shape.size) return data[position].toString()
        return (0 until shape[dim]).joinToString(prefix = "[", separator = ", ", postfix = "]") { i ->
            render(dim + 1, position + i * strides[dim])
        }
    }


    /** Position of [index] in data; no checks. */
    private fun offsetOf(index: IntArray): Int =
        index.foldIndexed(0) { idx, pos, element -> pos + element * strides[idx] }

    /** Checks [index] against the shape and returns its position in [data]. */
    private fun positionOf(index: IntArray): Int {
        require(index.size == shape.size) {
            "Index ${index.contentToString()} has ${index.size} values, but shape ${shape.contentToString()} has ${shape.size} dimensions"
        }
        index.forEachIndexed { k, i ->
            if (i !in 0 until shape[k]) {
                throw IndexOutOfBoundsException(
                    "Index ${index.contentToString()} is out of bounds for shape ${shape.contentToString()}",
                )
            }
        }
        return offsetOf(index)
    }

    companion object {
        /**
         * Row-major strides for [shape]: `strides[k]` is how many positions in data one step in
         * dimension k moves. The last dimension has stride 1. For shape [2, 3, 4]: [12, 4, 1].
         */
        fun contiguousStrides(shape: IntArray): IntArray {
            val result = IntArray(shape.size)
            var step = 1
            for (k in shape.indices.reversed()) {
                result[k] = step
                step *= shape[k]
            }
            return result
        }

        /** A tensor of the given shape with all values 0.0. `zeros()` is a scalar. */
        fun zeros(vararg shape: Int): Tensor {
            return Tensor(shape, DoubleArray(sizeOf(shape)))
        }

        /** A tensor of the given shape with all values 1.0. `ones()` is a scalar. */
        fun ones(vararg shape: Int): Tensor {
            return Tensor(shape, DoubleArray(sizeOf(shape)) { 1.0 })
        }


        /** Product of all dimensions; throws ArithmeticException on Int overflow. */
        private fun sizeOf(shape: IntArray): Int = shape.fold(1) { acc, d -> Math.multiplyExact(acc, d) }

        private fun IntArray.swapped(i: Int, j: Int): IntArray =
            this.clone().apply { this[i] = this[j].also { this[j] = this[i] } }
    }
}
