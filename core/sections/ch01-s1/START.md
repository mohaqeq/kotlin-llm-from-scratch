# ch01-s1 · Tensor basics

Guide: <https://mohaqeq.github.io/kotlin-llm-from-scratch/ch01/s1-tensor-basics/>

## What you build

The class `llm.core.Tensor`: an n-dimensional array of `Double` values with

- a shape and a size (with checks for wrong data, negative dimensions and `Int` overflow),
- row-major strides, and element access through them (`t[i, j]`, `t[i, j] = v`) with index checks,
- the factories `Tensor.zeros(...)` and `Tensor.ones(...)`,
- views that share the data: `reshape` and `transpose`,
- `isContiguous` and `contiguous()`, which copies a view into row-major order,
- `toString()` in nested brackets.

## Prerequisites

- Concepts: Kotlin classes, `IntArray`/`DoubleArray`, `vararg`, `require`, and recursion (for `toString`).
  No math beyond multiplication and division with remainder.
- Earlier classes that you use: none. This is the first section.

## Begin

```sh
./gradlew startSection -Psection=ch01-s1
```

Or: `git switch start/ch01-s1`.

Write your code in `core/src/exercise/kotlin/llm/core/Tensor.kt`. Then run:

```sh
./gradlew :core:test -Pmode=exercise -Psection=ch01-s1
```

## Tests to make green, in order

Tests are in `core/src/test/kotlin/llm/ch01/s1/`.

1. `TensorBasicsTest > size is the product of all dimensions`
2. `TensorBasicsTest > a scalar has an empty shape and size 1`
3. `TensorBasicsTest > the data length must match the shape`
4. `TensorBasicsTest > a negative dimension is rejected`
5. `TensorBasicsTest > a shape whose size overflows Int is rejected`
6. `TensorBasicsTest > row-major strides: the last dimension moves fastest`
7. `TensorBasicsTest > an element is read through the strides`
8. `TensorBasicsTest > a wrong index is rejected, not read from another position`
9. `TensorBasicsTest > an element is written through the strides`
10. `TensorBasicsTest > zeros and ones make a tensor of the shape, filled with 0.0 or 1.0`
11. `TensorBasicsTest > reshape makes a view with a new shape that shares the data`
12. `TensorBasicsTest > transpose swaps two dimensions by swapping their strides, without a copy`
13. `TensorBasicsTest > transpose rejects a dimension number that the tensor does not have`
14. `TensorBasicsTest > contiguous copies a view into row-major order; a contiguous tensor returns itself`
15. `TensorBasicsTest > reshape of a non-contiguous view keeps the row-major order of the view`
16. `TensorBasicsTest > toString prints nested brackets in the order of the view`

Property tests (`TensorPropertiesTest`, random shapes) check the same code on many cases:

17. `TensorPropertiesTest > transposing twice with the same dimensions gives back the original view`
18. `TensorPropertiesTest > a transposed view reads each element with the two indices swapped`
19. `TensorPropertiesTest > reshape to one dimension and back keeps every element in row-major order`
20. `TensorPropertiesTest > contiguous keeps every value and gives row-major strides`

## Done when

- All 20 tests of `llm.ch01.s1` pass in exercise mode.
- `./gradlew :core:run -Psection=1 -Pmode=exercise` prints the tensor, its transpose and its reshapes, not a TODO.
  The last line shows that a write to `t` is visible in the transposed view.
