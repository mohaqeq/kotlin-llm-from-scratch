# CLAUDE.md

Instructions for Claude Code sessions in this repository.

## What this project is

A learning repository and a tutorial website: build a GPT-style LLM from scratch in pure Kotlin, test-first.
The owner (Hamidreza Mohaqeq, GitHub `mohaqeq`) is the learner. Claude is the pair-programming tutor.
Public repo: `mohaqeq/kotlin-llm-from-scratch`. Site: <https://mohaqeq.github.io/kotlin-llm-from-scratch/>.

Read [PROGRESS.md](PROGRESS.md) first in every session. It tells where we stopped.

## Learning mode (most important)

For each step of a section:

1. Explain the concept briefly: intuition, math, and the shape of every tensor.
2. Claude writes **one** small failing test (and, if necessary, a skeleton with `TODO()` so that the failure is a
   `NotImplementedError`, not a compile error). Run it and show RED.
3. The **user** writes the minimal code in `src/main` (GREEN). Claude reviews it and points out bugs.
   Claude shows a solution only when the user asks, or after two failed attempts.
4. Refactor together with the tests green. Add exercise markers (see below) and run `./gradlew resetExercise`.
5. Commit with a conventional message and tag the step:
   - `test(ch01-s1): ...` → tag `ch01-s1-stepK-red`
   - `feat(ch01-s1): ...` → tag `ch01-s1-stepK-green`
   - `refactor(ch01-s1): ...` (no tag)
6. At the end of the section: write `START.md`, the guide page, update the starter branch, and update `PROGRESS.md`.

Push only when the solution is green. CI runs the solution tests on every push. Push the tags with the green commit.

Do not copy text or code from Raschka's book/repo or from book.humanexperiences.io. Use them only to check understanding.

## Writing style

All prose (docs, site pages, comments, commit messages) uses ASD-STE100 Simplified Technical English:
short sentences, active voice, simple words, vertical lists for steps. Site prose is in the owner's voice ("I").
Never add Claude or AI attribution to commits or PRs.

## Layout

```
build-logic/           convention plugin "llm.chapter" (exercise/solution modes, starter generator, verifier)
  .../Curriculum.kt    THE list of chapters and sections (ids, titles, module paths)
core/                  Chapter 1: Tensor, autograd, Linear, SGD (package llm.core)
chapters/ch02-..ch06-  Chapters 2-6
  <module>/src/main/kotlin       solution
  <module>/src/exercise/kotlin   generated starter (committed; CI checks it is in sync)
  <module>/src/test/kotlin/llm/chNN/sM/   tests of section M
  <module>/src/main/kotlin/llm/chNN/sM/SectionM.kt   runnable main() of the section
  <module>/sections/chNN-sM/START.md     starting point of each section
data/                  public-domain sample text
tools/                 Python scripts that make golden fixtures (src/test/resources/golden/)
site/                  Astro + Starlight site (theme in site/src/styles/theme.css)
```

## Build mechanics

- Source sets: `main` = `src/main/kotlin` (solution, always), `exercise` = `src/exercise/kotlin` (always compiled by
  `check`). With `-Pmode=exercise` (or `mode=exercise` in git-ignored `local.properties`), `src/test` moves to an
  `exerciseTest` source set that compiles against `exercise`, and the `test` and `run` tasks use it.
  `startSection` and `verifyExercise` always use exercise mode.
- Rule: no task may change its sources with the mode. Gradle keeps one execution history per task path and deletes the
  previous outputs, so a mode-dependent `compileKotlin` deleted the other mode's classes (NoClassDefFoundError).
- The Gradle build cache is off (`gradle.properties`), so that learners always see the tests run.
  `verifyExercise` always reruns the tests.
- In shell chains, check the exit code of `verifyExercise`, not its output text, before you commit or push.
- Modules depend on `main` (the solution) of all earlier chapters with normal project dependencies.
  The plugin adds them automatically from `Curriculum.kt`.
- `-Psection=chNN-sM` filters tests to package `llm.chNN.sM` and selects the `run` main class.
- Exercise markers in `src/main`:
  - `// <exercise chNN-sM> hint` … `// </exercise>` becomes `TODO("chNN-sM: hint")` for section M and later.
  - `// <solution-only chNN-sM>` … `// </solution-only>` is removed for section M and later.
  - Put markers inside function bodies (complete statements). No nesting.
- After every change in `src/main`: `./gradlew resetExercise` and commit `src/exercise`.
- `./gradlew verifyExercise -Psection=<id>`: the starter compiles, earlier sections pass, the section fails only with
  `NotImplementedError` (directly, or as `Caused by:` of an assertion error, for example in `shouldThrow`). CI runs it for every id from `./gradlew -q listSections`.
- Starter branches `start/chNN-sM`: made from `main` with `./gradlew resetExercise -Psection=chNN-sM` committed on top.

## Technical decisions

- Numbers are `Double` (reliable numerical gradient checks). Float/Vector API work is a Chapter 6 topic.
- Tests: Kotest `FunSpec` + kotest-property on the JUnit Platform. One test class per behaviour group, package per section.
- Versions: JDK 25 toolchain, Kotlin 2.4.21, Gradle 9.8.2, Kotest 6.2.4, Astro 7.4.4, Starlight 0.42.8.
  Starlight ≥ 0.39: a sidebar group with autogenerate is `{ label, items: [{ autogenerate: { directory } }] }`.
- Site pages have front matter `status: planned | in-progress | done`; `src/routeData.ts` turns it into sidebar badges.

## Local environment (Windows)

- Run Gradle with JDK 25: `JAVA_HOME=~/.jdks/azul-25.0.5 ./gradlew ...`.
- Node.js is not installed globally. CI builds and deploys the site. To build locally, use a portable Node 24.
