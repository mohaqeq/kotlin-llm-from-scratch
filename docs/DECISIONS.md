# Decisions

Why the project is built this way. Newest entries go at the end. Each entry: the decision, the reason, and what not to
do again.

## D1. One Gradle module per chapter; `core` is Chapter 1

- A module holds all sections of its chapter. Tests and section demos live in a package per section (`llm.chNN.sM`),
  so `-Psection` can filter them.
- Chapter 1 is the module `core`, because later chapters use its tensor, autograd and layers as a library.

## D2. One source of truth for solution and starter: exercise markers

- The solution in `src/main` has marked regions. `StarterGenerator` replaces them with `TODO()` for the target
  section and later ones, and keeps them for earlier sections. `src/exercise` is generated and committed.
- Reason: no second copy of the code that can drift. Every starter is reproducible from `main`.
- CI regenerates the starters and fails on any difference.

## D3. `main` is always the solution; exercise mode adds an `exerciseTest` source set

- First design: the mode switched the source folder of the `main` source set. **Do not do this again.** Gradle keeps
  one execution history per task path. `compileKotlin` then compiled different folders into different places, and
  Gradle deleted the outputs of the other mode. Tests failed with `NoClassDefFoundError`, and `verifyExercise` read
  stale results. A separate build folder and turning off incremental compilation only hid the problem.
- Current design: `main` and `exercise` are fixed source sets with their own compile tasks. In exercise mode,
  `src/test` moves to `exerciseTest`, which compiles against `exercise`; `test` and `run` use it.
- Rule: no task may change its sources with the mode.

## D4. Gradle build cache off

- The cache once stored a wrong output (from the D3 bug), and learners could see "FROM-CACHE" and think the tests did
  not run. The modules are small, so the cost is low.

## D5. The verifier accepts `NotImplementedError` as a cause

- `shouldThrow<X> { ... }` catches the `TODO()` error and reports an assertion error with
  `Caused by: kotlin.NotImplementedError`. The verifier reads the failure text and accepts that cause.

## D6. `Double`, not `Float`

- Numerical gradient checks are unreliable in `Float` (PyTorch's `gradcheck` also needs double precision).
- Memory for GPT-2 small (124M parameters) is about 1 GB, which is acceptable. `Float` and the Vector API are Ch 6
  performance topics.

## D7. Tensor design (ch01-s1)

- `Tensor(shape: IntArray, data: DoubleArray, strides: IntArray = contiguousStrides(shape))`.
- `size` is a stored value with an overflow check (`Math.multiplyExact`). Negative dimensions are rejected.
- Views share `data`: `reshape` (via `contiguous()` when the tensor is not contiguous) and `transpose` (swaps shape
  and strides). `contiguous()` returns `this` when the strides are row-major.
- `get`/`set` check the index count (`IllegalArgumentException`) and each index range (`IndexOutOfBoundsException`).
  `positionOf` checks, and `offsetOf` computes the position without checks for inner loops.
- The owner chose `by lazy` once; it was changed to `run { }` because `init` always reads `size`.

## D8. CI runs on branch pushes only

- A RED-step tag points at a commit with a failing test on purpose. CI on tags would always fail there.
- `start/*` branches hold a section starter in `src/exercise` on purpose, so the sync check would fail. CI ignores them.

## D9. Formatting with Spotless + ktlint; no detekt

- The owner asked for automatic formatting and released tools only.
- Detekt 1.23.8 (the only release) supports Kotlin 2.0, not 2.4. Detekt 2.x is alpha. So: no detekt.
- Kotlin compiler warnings are errors instead (catches shadowed names and unused variables). Qodana is a stable option
  for deeper analysis later.
- ktlint rules: `intellij_idea` style, 120 columns, trailing commas allowed. `class-signature` is off (it re-indents
  kotest specs). `no-consecutive-comments` is off (exercise markers sit directly above KDoc).
- `.editorconfig` is passed to Spotless explicitly. Without it, the Gradle daemon kept old rules in a cache.

## D10. Site: Astro + Starlight, static, deployed by Actions

- Starlight gives sidebar, search and dark mode with little code. The built files are not committed: the workflow
  uploads `site/dist/` as an artifact, and GitHub Pages serves it.
- Front matter `status` and a route middleware make the sidebar badges.
- Headings use JetBrains Mono (700, -0.02em) to match the owner's blog. The serif heading font (Fraunces) was removed.

## D11. Versions

- Pick stable releases that are at least two weeks old.
- Current: JDK 25, Kotlin 2.4.21, Gradle 9.8.2, Kotest 6.2.4 (JUnit Platform 1.13 from Kotest), Spotless 8.9.0,
  ktlint 1.8.0, Astro 7.4.4, Starlight 0.42.8, Node 24 LTS. GitHub Actions: checkout v7, setup-java v6,
  gradle/actions v6, setup-node v7, configure-pages v6, upload-pages-artifact v5, deploy-pages v5.
- Known deprecation: Spotless calls `Configuration.setVisible`, deprecated for Gradle 11. It is inside the plugin.
