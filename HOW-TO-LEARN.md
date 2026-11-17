# How to learn with this repository

Each section is a small test-driven project. You write the code. The tests tell you when it is correct.
The website has the guide for each section: <https://mohaqeq.github.io/kotlin-llm-from-scratch/>

## Prerequisites

- JDK 25. If Gradle does not find it, Gradle downloads it.
- Git.
- An IDE with Kotlin support (optional), for example IntelliJ IDEA.

You do not need Python or a GPU.

## The layout

| Module                     | Chapter                     |
| -------------------------- | --------------------------- |
| `core/`                    | 1 · Foundations             |
| `chapters/ch02-tokenizer/` | 2 · Tokenizer and data      |
| `chapters/ch03-attention/` | 3 · Attention               |
| `chapters/ch04-gpt/`       | 4 · GPT model               |
| `chapters/ch05-training/`  | 5 · Training                |
| `chapters/ch06-bonus/`     | 6 · Bonus                   |

Each module has two source folders:

- `src/main/kotlin`: the solution.
- `src/exercise/kotlin`: your code. It starts as skeletons with `TODO()` bodies.

The tests in `src/test/kotlin/llm/chNN/sM/` are for section M of chapter NN. They are the same for both folders.

A module always uses the **solution** of earlier chapters, never your exercise code.
Thus your work in Chapter 4 does not depend on your work in Chapter 3.

## Start any section

1. Read `<module>/sections/<section>/START.md`, for example [core/sections/ch01-s1/START.md](core/sections/ch01-s1/START.md).
2. Run:

   ```sh
   ./gradlew startSection -Psection=ch01-s1
   ```

   This command resets `src/exercise` of that chapter to the starter state of the section.
   Then it runs the tests of the section. They fail with `NotImplementedError` (RED).
   The starter state contains the solution of all earlier sections of the chapter.

As an alternative, switch to the starter branch: `git switch start/ch01-s1`.

> **Caution:** `startSection` and `resetExercise` overwrite `src/exercise` of that chapter.
> Commit or stash your work first if you want to keep it.

## Run the tests on your code

```sh
./gradlew :core:test -Pmode=exercise -Psection=ch01-s1
```

- Without `-Pmode=exercise`, Gradle runs the tests on the solution.
- Without `-Psection`, Gradle runs the tests of all sections of the module.
- To use exercise mode in your IDE too, create `local.properties` in the repository root with the line `mode=exercise`.
  This file is git-ignored.

## Reset an exercise

```sh
./gradlew resetExercise -Psection=ch01-s1   # one section
./gradlew resetExercise                     # all chapters, to their first section
```

## Compare with the solution

- The solution is in `src/main/kotlin` of the same module.
- Each TDD step has two git tags: `chNN-sM-stepK-red` (the new failing test) and `chNN-sM-stepK-green` (the code that passes).

  ```sh
  git tag --list 'ch01-s1-*'
  git diff ch01-s1-step1-red ch01-s1-step1-green
  ```

## Run the demo of a section

```sh
./gradlew :core:run -Psection=1
./gradlew :chapters:ch03-attention:run -Psection=4 -Pmode=exercise   # on your code
```

If the code still has a `TODO()`, the demo tells you what to implement.
