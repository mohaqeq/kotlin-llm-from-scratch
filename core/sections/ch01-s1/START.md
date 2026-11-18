# ch01-s1 · Tensor basics

> Status: in progress. The list of tests grows with each TDD step.

Guide: <https://mohaqeq.github.io/kotlin-llm-from-scratch/ch01/s1-tensor-basics/>

## What you build

The class `llm.core.Tensor`: an n-dimensional array of `Double` values with a shape, a size,
row-major strides, element access, and views (reshape, transpose) that share the data.

## Prerequisites

- Concepts: Kotlin classes, arrays, and `vararg`. No math beyond multiplication.
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

## Done when

- All tests of `llm.ch01.s1` pass in exercise mode.
- `./gradlew :core:run -Psection=1 -Pmode=exercise` prints the shape and the size, not a TODO.
