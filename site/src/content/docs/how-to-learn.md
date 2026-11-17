---
title: How to learn with this site
description: The exercise and solution workflow, and how to start at any section.
---

Each section of this tutorial is a small TDD project. You write the code. The tests tell you when it is correct.

## What you need

- JDK 25. Gradle downloads it if you do not have it.
- Git.
- An IDE with Kotlin support, for example IntelliJ IDEA. This is optional.

You do not need Python or a GPU.

## Two folders in each module

Each chapter module has two source folders:

| Folder                | Contents                                                |
| --------------------- | ------------------------------------------------------- |
| `src/main/kotlin`     | The solution.                                           |
| `src/exercise/kotlin` | Your code. It starts as skeletons with `TODO()` bodies. |

The tests in `src/test/kotlin` are the same for both folders.
Chapter 1 is the module `core`. Chapters 2 to 6 are in `chapters/`.

## Start a section

```sh
./gradlew startSection -Psection=ch01-s1
```

This command does two things:

1. It resets `src/exercise` of that chapter to the starter state of the section.
2. It runs the tests of the section. They fail with `NotImplementedError` (RED).

The starter state contains the solution of all earlier sections. Thus you can start at any section.

:::caution
`startSection` and `resetExercise` overwrite `src/exercise` of that chapter.
Commit or stash your work first if you want to keep it.
:::

As an alternative, switch to the starter branch of the section:

```sh
git switch start/ch01-s1
```

## Make the tests green

Write your code in `src/exercise/kotlin`. Then run the tests of the section on your code:

```sh
./gradlew :core:test -Pmode=exercise -Psection=ch01-s1
```

Without `-Pmode=exercise`, Gradle runs the tests on the solution.
To use exercise mode in your IDE too, create the file `local.properties` in the repository root with this line:

```properties
mode=exercise
```

## Compare with the solution

The solution of every section is in `src/main/kotlin` of the same module.
Each TDD step has two git tags, for example `ch01-s1-step1-red` and `ch01-s1-step1-green`.
This command shows one step:

```sh
git diff ch01-s1-step1-red ch01-s1-step1-green
```

## Run the demo of a section

```sh
./gradlew :core:run -Psection=1
```

If your code still has a `TODO()`, the demo tells you what to implement.
