# Contributing

Thank you for your interest. Read [HOW-TO-LEARN.md](HOW-TO-LEARN.md) first. It explains the exercise and solution folders.

## Rules for changes

1. Every behaviour starts with a failing test (red → green → refactor).
2. Mark the code that a learner writes with exercise markers (see below).
3. After you change `src/main`, run `./gradlew resetExercise` and commit the changed `src/exercise` files.
   CI fails if `src/exercise` is not the generated starter state.
4. Run `./gradlew build` and `./gradlew verifyExercise -Psection=<id>` for each section that you changed.
5. Use conventional commit messages with the section as scope, for example `feat(ch03-s2): add causal mask`.

## Exercise markers

```kotlin
fun numel(): Int {
    // <exercise ch01-s1> Multiply all dimensions of the shape.
    return shape.fold(1) { acc, d -> acc * d }
    // </exercise>
}
```

For section `ch01-s1` and all later sections of the same chapter, the generator replaces the region with
`TODO("ch01-s1: Multiply all dimensions of the shape.")`. For an earlier section, it keeps the code.

Use `// <solution-only ch01-s1>` … `// </solution-only>` for code that must not appear in the starter at all,
for example a private helper of the solution.
