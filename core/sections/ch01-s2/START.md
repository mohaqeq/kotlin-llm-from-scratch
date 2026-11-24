# ch01-s2 · Element-wise operations and broadcasting

> Status: in progress. The list of tests grows with each TDD step.

Guide: <https://mohaqeq.github.io/kotlin-llm-from-scratch/ch01/s2-elementwise-broadcasting/>

## What you build

Element-wise operations on `llm.core.Tensor` (`a + b` first), and broadcasting: tensors of different but compatible
shapes combine without copies.

## Prerequisites

- Concepts: element-wise arithmetic; Kotlin `operator` functions.
- Earlier classes that you use: `Tensor` from ch01-s1 (shape, strides, `get`/`set`, views). The starter has its
  solution.

## Begin

```sh
./gradlew startSection -Psection=ch01-s2
```

Or: `git switch start/ch01-s2`.

Write your code in `core/src/exercise/kotlin/llm/core/Tensor.kt`. Then run:

```sh
./gradlew :core:test -Pmode=exercise -Psection=ch01-s2
```

## Tests to make green, in order

Tests are in `core/src/test/kotlin/llm/ch01/s2/`.

1. `ElementwiseTest > plus adds two tensors of the same shape element by element`
2. `ElementwiseTest > plus adds by index, also when an input is a view`
3. `ElementwiseTest > plus rejects tensors whose shapes do not fit, and the message names both shapes`
4. `BroadcastTest > broadcastShapes aligns the shapes from the right, and a dimension of 1 stretches`

## Done when

- All tests of `llm.ch01.s2` pass in exercise mode.
- `./gradlew :core:run -Psection=2 -Pmode=exercise` prints `a + b`, not a TODO.
