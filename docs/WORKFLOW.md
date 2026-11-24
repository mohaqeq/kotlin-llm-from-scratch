# Workflow

Exact procedures for the agent. The rules behind them are in [AGENTS.md](../AGENTS.md).
In the examples, the section is `ch01-s1` and the module is `:core`. Replace them for other sections.

## 1. Start a session

1. Read `PROGRESS.md`. It names the current section, the last step, and the next action.
2. Run `git status` and `git log --oneline -5`. Uncommitted changes are usually the owner's work in progress.
   Do not revert them. Ask if their purpose is not clear.
3. Run `./gradlew :core:test -Psection=ch01-s1` to see the current state (green, or one RED test).

## 2. One TDD step

### 2.1 RED (the agent)

1. Explain the concept briefly: intuition, math, and the shape of every tensor (a small table helps).
2. Write **one** test in `src/test/kotlin/llm/chNN/sM/`. Test one behaviour; several assertions are fine.
3. If the test needs a new function or property, add a skeleton in `src/main` with the body inside markers:

   ```kotlin
   fun transpose(dim0: Int, dim1: Int): Tensor {
       // <exercise ch01-s1> Swap the two dimensions in a copy of the shape and of the strides; share the data.
       TODO("ch01-s1: swap two dimensions through the strides")
       // </exercise>
   }
   ```

   Then the test fails with `NotImplementedError`, not with a compile error. A getter must not throw from a stored
   property initializer: it would break every earlier test.
4. Add the test name to `sections/chNN-sM/START.md` ("Tests to make green, in order").
5. Run the tests. Show the RED output to the owner.
6. Run `./gradlew resetExercise`, then `./gradlew verifyExercise -Psection=ch01-s1`. Continue only if the exit code is 0.
7. Commit locally and tag. **Do not push.**

   ```sh
   git add core
   git commit -m "test(ch01-s1): transpose swaps two dimensions through the strides"
   git tag ch01-s1-step11-red
   ```

8. Tell the owner what to implement, where (file and markers), the command to run, and a hint (not the solution).

### 2.2 GREEN (the owner)

The owner writes the code in `src/main` and says "green". Then the agent:

1. Reads the diff: `git diff core/src/main`.
2. Runs the section tests, `resetExercise` and `verifyExercise`. Checks the exit codes.
3. Reads the generated starter for leaks: new private helpers must be inside `solution-only` markers; no solution
   code outside markers. Fix marker placement directly (it changes no behaviour), and tell the owner.
4. Commits, tags and pushes the step:

   ```sh
   git add core
   git commit -m "feat(ch01-s1): transpose two dimensions by swapping shape and strides"
   git tag ch01-s1-step11-green
   git push origin main ch01-s1-step11-red ch01-s1-step11-green
   ```

5. Reviews the code for the owner: bugs first, then design, naming, performance, style. Explain why.
   Point out a bug that a later test will catch, and write that test next.

If the owner's attempt fails, explain the cause precisely (a trace table works well). Show a full solution only when
the owner asks, or after two failed attempts.

### 2.3 REFACTOR (together)

1. Propose the refactor with a short code example. The owner usually applies it.
2. Tests must stay green with no change to the tests. New behaviour (for example a new `require`) needs a test in the
   same commit.
3. Run tests, `resetExercise`, `verifyExercise`; read the starter; commit `refactor(ch01-s1): ...` and push. No tag.

### 2.4 A refactor that arrives after the next RED commit

The diff between `stepK-red` and `stepK-green` must show only step K. If the owner refactors step K-1 code while the
RED commit of step K is local:

1. Stash or set aside the refactor. Create a branch at the previous green tag:
   `git checkout -b reorder ch01-s1-step(K-1)-green`.
2. Apply the refactor there, run the checks, commit it.
3. `git cherry-pick ch01-s1-stepK-red`. Resolve conflicts (usually two tests added at the same place: keep both).
4. Check that the RED state is still correct (only the new test fails; `verifyExercise` exits 0).
5. `git branch -f main reorder && git checkout main && git branch -D reorder`.
6. Move the tag: `git tag -f ch01-s1-stepK-red HEAD`.
7. Push only the refactor commit: `git push origin <refactor-sha>:main`. The RED commit stays local.

If the refactor fits the next step naturally, tell the owner to include it in the GREEN of step K instead.

## 3. Close a section

Do these when all planned steps of the section are green.

1. **Property tests.** Add a `<Name>PropertiesTest` with kotest-property for the invariants of the section. They usually
   pass at once: commit as `test(chNN-sM): ...` with no step tag.
2. **START.md.** In `sections/chNN-sM/START.md`: remove the "in progress" status line, complete the test list, check
   the prerequisites (concepts, and earlier classes used), the begin command, and "Done when".
3. **Demo.** Make `SectionM.kt` show the main result of the section. Check
   `./gradlew :core:run -Psection=M` (solution) and `-Pmode=exercise` after `startSection` (prints a TODO line).
4. **Guide page.** Write `site/src/content/docs/chNN/sM-<slug>.mdx` as a TDD walkthrough (template below).
   Set `status: done`. Set the chapter overview `chNN/index.md` to `done` when its last section is done.
5. **Starter branch.** Create or update `start/chNN-sM` (procedure below).
6. **PROGRESS.md.** Mark the section `[x]`, add a session log entry, and write the next action.
7. Run `./gradlew build`, push, and check that CI and the site deploy are green: `gh run list --limit 3`.

### Guide page template

Each section page has, in this order:

1. A "Start here" box: the `startSection` command, the `git switch start/chNN-sM` alternative, the prerequisites.
2. The concept: intuition, math, a tensor-shape table, and a diagram (Mermaid or SVG) when it helps.
3. One part per TDD step: "Write this test" (the test) → "Watch it fail" (the RED output) → a hint → a collapsible
   solution (`<details>`) → the refactor.
4. "Run it": the demo command and its output.
5. Exercises for the reader, and a link to the source on GitHub.

Code on the page must come from the repository files, not from copies, so the site never drifts from the code.
**Not built yet:** a `Snippet` component (`site/src/components/Snippet.astro`) that reads a repository file at build
time (with `node:fs` from the repository root, or `import.meta.glob` with `?raw`) and shows a marked region. Plan:
region markers such as `// region snippet-name` … `// endregion` in the Kotlin sources, which the starter generator
must ignore. Build it with the first guide page (ch01-s1).

### Starter branch procedure

The starter branch holds `main` plus the starter state of the section in `src/exercise`.

```sh
git switch -C start/ch01-s1 main
./gradlew resetExercise -Psection=ch01-s1
git add -A && git commit -m "chore(ch01-s1): starter state"   # skip when nothing changed (first section of a chapter)
git push --force origin start/ch01-s1
git switch main
```

A force-push is correct here: a starter branch is always regenerated from `main`. Update the starter branches of a
chapter again when its earlier code changes. CI does not run on `start/*` branches.

## 4. End a session

1. Update `PROGRESS.md`: what changed today, the exact next action, and any open review points.
2. Commit and push only green work. A local RED commit is fine: say so in `PROGRESS.md`.

## 5. Add or change a tool or dependency

1. Check the latest stable release and its date (Maven Central, Gradle plugin portal, npm registry).
   Use a release that is at least two weeks old. No alpha, beta or RC versions.
2. Add the version to `gradle/libs.versions.toml` (or `site/package.json` with the lockfile updated).
3. Run `./gradlew build --warning-mode all` and check for new deprecation warnings.
4. Record the decision in `docs/DECISIONS.md`.
