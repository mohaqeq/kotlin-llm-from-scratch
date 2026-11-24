# AGENTS.md

Instructions for every coding agent in this repository (Claude Code, Codex, Cursor, Copilot, Gemini, and others).
This file is the single source of truth. Tool-specific files (`CLAUDE.md`, `GEMINI.md`,
`.github/copilot-instructions.md`) only point here.

## Read first, in this order

1. [PROGRESS.md](PROGRESS.md): where the work stopped and the next action. Read it at the start of every session.
2. This file: the rules and the commands.
3. [docs/WORKFLOW.md](docs/WORKFLOW.md): the exact procedures (one TDD step, closing a section, branches, tags).
4. [docs/PROJECT-BRIEF.md](docs/PROJECT-BRIEF.md): the goals and the full scope (curriculum, site, blog post).
5. [docs/DECISIONS.md](docs/DECISIONS.md): why the project is built this way, and the mistakes not to repeat.

## What this project is

A learning repository and a public tutorial site: build a GPT-style LLM from scratch in pure Kotlin, test-first.

- Owner and learner: Hamidreza Mohaqeq (GitHub `mohaqeq`). The agent is a pair-programming **tutor**.
- Repository: <https://github.com/mohaqeq/kotlin-llm-from-scratch> (public). Default branch: `main`.
- Site: <https://mohaqeq.github.io/kotlin-llm-from-scratch/> (Astro + Starlight, deployed by GitHub Actions).
- Readers can start at any section: each section has a starter state with tests and `TODO()` skeletons.

## The owner's rules (always apply)

1. **Learning mode.** The owner writes the solution code. The agent explains, writes one failing test at a time,
   reviews, and refactors together with the owner. The agent writes solution code only when the owner asks, or after
   two failed attempts. Details: [docs/WORKFLOW.md](docs/WORKFLOW.md).
2. **Writing style.** All text (answers, docs, site pages, code comments, commit messages) uses ASD-STE100
   Simplified Technical English: short sentences (20 words maximum for procedures, 25 for descriptions), one topic per
   sentence, active voice, simple tenses, simple words with one meaning, vertical lists for steps. Site prose is in the
   owner's voice ("I").
3. **No AI attribution.** Never add `Co-Authored-By` lines for an AI, "Generated with ..." lines, or similar markers to
   commits or pull requests.
4. **Original work.** Raschka's *Build a Large Language Model (From Scratch)* and book.humanexperiences.io are
   references only. Do not copy their text or code.
5. **Released tools only.** Use stable releases, not alpha/beta/RC. Pick versions that are at least two weeks old.
6. **Push only green.** CI runs the solution tests on every push to a branch. Keep RED commits local until the step is
   green, then push the commits and the step tags together.
7. **Confirm outward actions.** Ask before actions that are hard to undo or visible to others (force-push to `main`,
   deleting branches or tags, new repositories, posts). Routine pushes of green work to `main` are allowed.

## Commands

Run all commands from the repository root. On the owner's Windows machine, Gradle needs JDK 25:
`JAVA_HOME=~/.jdks/azul-25.0.5 ./gradlew ...` (Git Bash). Gradle downloads JDK 25 if it is missing (foojay).

| Purpose | Command |
|---|---|
| Build everything, all tests, format check | `./gradlew build` |
| Tests of one section (solution) | `./gradlew :core:test -Psection=ch01-s1` |
| Tests of one section (learner code) | `./gradlew :core:test -Pmode=exercise -Psection=ch01-s1` |
| Demo of a section | `./gradlew :core:run -Psection=1` |
| Regenerate all starters (`src/exercise`) | `./gradlew resetExercise` |
| Starter of one section, then its tests (RED) | `./gradlew startSection -Psection=ch01-s1` |
| Check a starter (compiles, fails only with TODO) | `./gradlew verifyExercise -Psection=ch01-s1` |
| Sections that have tests | `./gradlew -q listSections` |
| Format the code | `./gradlew spotlessApply` (local compiles also do it) |
| Site, local (needs Node 24) | `cd site && npm ci && npm run dev` |

Module path per chapter: `:core` (Ch 1), `:chapters:ch02-tokenizer`, `:chapters:ch03-attention`, `:chapters:ch04-gpt`,
`:chapters:ch05-training`, `:chapters:ch06-bonus`.

## Layout

```
AGENTS.md, PROGRESS.md      agent rules; current state and next action
docs/                       PROJECT-BRIEF.md, WORKFLOW.md, DECISIONS.md
build-logic/                convention plugin "llm.chapter"
  .../Curriculum.kt         THE list of chapters and sections (ids, titles, module paths)
  .../ChapterPlugin.kt      source sets, modes, -Psection, tasks resetExercise/startSection/verifyExercise
  .../StarterGenerator.kt   makes src/exercise from src/main (exercise markers)
  .../ExerciseVerifier.kt   checks a starter from JUnit XML results
  .../CodeQuality.kt        Spotless/ktlint, warnings as errors, auto-format before compile
core/                       Chapter 1 (package llm.core)
chapters/ch02-..ch06-*/     Chapters 2-6 (empty until started)
  <module>/src/main/kotlin                 the solution
  <module>/src/exercise/kotlin             generated starter (committed; never edit by hand)
  <module>/src/test/kotlin/llm/chNN/sM/    tests of section M (package = section)
  <module>/src/main/kotlin/llm/chNN/sM/SectionM.kt   runnable main() of the section
  <module>/sections/chNN-sM/START.md       starting point of the section
data/                       public-domain sample text (The Verdict)
tools/                      Python scripts that make golden fixtures (not used yet)
site/                       tutorial site; pages in site/src/content/docs/chNN/
.github/workflows/          ci.yml (build, starter sync, starter checks), site.yml (Pages deploy)
```

## Build mechanics (read before you change build-logic or src/main)

- **Source sets.** `main` = `src/main/kotlin` (solution, always). `exercise` = `src/exercise/kotlin` (always compiled
  by `check`). With `-Pmode=exercise` (or `mode=exercise` in the git-ignored `local.properties`), `src/test` moves to an
  `exerciseTest` source set that compiles against `exercise`, and `test`/`run` use it. `startSection` and
  `verifyExercise` always use exercise mode.
- **Rule: no task may change its sources with the mode.** Gradle keeps one history per task path and deletes the
  previous outputs. A mode-dependent `compileKotlin` deleted the other mode's classes (NoClassDefFoundError).
- **Dependencies.** Each module depends on `main` (the solution) of all earlier chapters. The plugin adds them from
  `Curriculum.kt`. A learner's Ch 4 work never depends on the learner's Ch 3 work.
- **Exercise markers** in `src/main`:
  - `// <exercise chNN-sM> hint` … `// </exercise>` becomes `TODO("chNN-sM: hint")` in the starter of section M and
    later. Earlier sections keep the code.
  - `// <solution-only chNN-sM>` … `// </solution-only>` is removed in the starter of section M and later.
  - `exercise` markers go **inside** a function body, around complete statements. For a stored property use
    `val x = run { // <exercise ...> ... // </exercise> }`. A marker around a declaration breaks the starter.
  - **Every private helper** that the solution adds needs `solution-only` markers. `verifyExercise` cannot detect
    such a leak. After every green step, read the generated starter.
  - No nested markers.
- After every change in `src/main`: run `./gradlew resetExercise` and commit `src/exercise` with it. CI fails when
  `src/exercise` differs from the generated starter.
- `verifyExercise` rules: the starter compiles; tests of earlier sections pass; tests of the section fail, and only
  with `NotImplementedError` (directly, or as `Caused by:` of an assertion error, for example inside `shouldThrow`).
  In shell chains, check its **exit code**, never its output text, before you commit or push.
- Gradle build cache is off, so learners always see the tests run. `verifyExercise` always reruns tests.

## Code conventions

- Kotlin 2.4.21, JDK 25 toolchain, Gradle 9.8.2 (wrapper). Numbers are `Double` (reliable gradient checks).
- Tests: Kotest 6.2.4 `FunSpec` and kotest-property on the JUnit Platform. Package = section (`llm.ch01.s1`).
  Test names are sentences that state the behaviour.
- Formatting: Spotless 8.9.0 runs ktlint 1.8.0 with rules from `.editorconfig` (`intellij_idea` style, 120 columns,
  `class-signature` and `no-consecutive-comments` off). Local compiles run `spotlessApply` first; CI runs
  `spotlessCheck`. ktlint only breaks long lines: read the diff and rewrite awkward wraps by hand.
- Kotlin compiler warnings are errors in `src/main` and `src/test` (not in the generated `src/exercise`).
- Error messages name the values: `"Shape [2, 3] needs 6 values, but data has 5"`.
- Public API has KDoc in STE. Prefer clear names over clever idioms.

## Git conventions

- Conventional commits with the section as scope: `test(ch01-s1): ...`, `feat(ch01-s1): ...`,
  `refactor(ch01-s1): ...`. Other scopes: `build`, `ci`, `docs`, `style(site)`, `chore`, `fix(build)`.
- Step tags: `chNN-sM-stepK-red` on the test commit, `chNN-sM-stepK-green` on the code commit.
  `git diff <red> <green>` must show only that step: move unrelated commits before the RED commit (procedure in
  docs/WORKFLOW.md).
- Starter branches: `start/chNN-sM` (CI ignores them; see docs/WORKFLOW.md).
- CI runs on branch pushes only, not on tags.

## Site conventions

- Pages: `site/src/content/docs/chNN/sM-<slug>.md(x)`; chapter overview `chNN/index.md`.
- Front matter `status: planned | in-progress | done`. `site/src/routeData.ts` turns it into sidebar badges.
- Code on a page comes from the repository through `site/src/components/Snippet.astro` (usage in docs/WORKFLOW.md,
  "Guide page template"). Never paste copies of repository code into a page.
- Theme "Lab notebook" in `site/src/styles/theme.css`: warm paper, deep teal, Source Serif body, JetBrains Mono
  headings (700, letter-spacing -0.02em, as on the owner's blog), JetBrains Mono code.
- Starlight 0.42.8 / Astro 7.4.4. Sidebar groups use `{ label, items: [{ autogenerate: { directory } }] }`.
- Node.js is not installed globally on the owner's machine. CI builds and deploys. For a local build, download a
  portable Node 24 into a temporary folder.
