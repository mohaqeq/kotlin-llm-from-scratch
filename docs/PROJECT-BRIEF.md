# Project brief

The goals and the full scope of the project, from the owner's original request (November 2026).
Status marks: ✅ done, 🟡 in progress, ⬜ not started. The detailed checklist is in [PROGRESS.md](../PROGRESS.md).

## Goals

1. The owner learns how to build a GPT-style LLM from scratch in Kotlin, with strict TDD.
2. Any reader can clone the repository and follow the same path, starting at any section.
3. A public tutorial site has a guide and runnable code for every section. It is similar in spirit to
   book.humanexperiences.io (a Node.js/TypeScript book), but in Kotlin, with original text and a different look.
4. At the end, a blog post on the owner's blog describes the project.

## 1. Learning mode: strict TDD, reusable by anyone

- ✅ The red → green → refactor loop for each step (details in [WORKFLOW.md](WORKFLOW.md)).
- Test kinds:
  - ✅ example tests with small hand-computed tensors,
  - ✅ property tests (kotest-property) for invariants,
  - ⬜ numerical gradient checks for every layer with a backward pass (from ch01-s4),
  - ⬜ golden tests against values exported once from PyTorch: small JSON fixtures in `src/test/resources/golden/`,
    and the Python script that made them in `tools/`.
- ✅ Two states per section on `main`: the starter (`src/exercise`, tests fail with `TODO()`) and the solution
  (`src/main`). `-Pmode=exercise` switches the tests to the learner's code.
- ✅ Every section is a self-contained starting point: a section uses the solution of all earlier sections.
- ✅ Each section has `sections/chNN-sM/START.md`: what you build, prerequisites, the begin command, the tests in
  order, "done when". (ch01-s1 exists.)
- ✅ `./gradlew startSection -Psection=<id>` resets the starter and shows RED. `resetExercise` resets.
- 🟡 Starter files include everything except the part being learned, and a runnable `main()` that prints a clear TODO.
- ✅ A branch `start/chNN-sM` per section with the starter state (made when a section closes).
- ✅ CI checks every section starter from a fresh clone: it compiles, it fails only with `NotImplementedError`, and the
  solution passes.
- ✅ Git tags for every TDD step (`chNN-sM-stepK-red`, `chNN-sM-stepK-green`).
- ✅ `HOW-TO-LEARN.md` and `CONTRIBUTING.md` at the root.
- ✅ `PROGRESS.md`, updated at the end of every session.

## 2. Repository setup

- ✅ Repository `mohaqeq/kotlin-llm-from-scratch` (public, MIT).
- ✅ Gradle Kotlin DSL multi-module build. JDK 25 (the owner changed this from 21), latest stable Kotlin.
- ✅ Layout: `core/` (Ch 1), `chapters/ch02..ch06`, `site/`, `data/` (public-domain "The Verdict"), `tools/`.
- ✅ Each section has a `main()` (`SectionN.kt`), runnable with `./gradlew :<module>:run -Psection=N`.
- Libraries: pure Kotlin for everything built from scratch. Allowed helpers:
  - ⬜ JTokkit, to check the BPE tokenizer against GPT-2 (Ch 2),
  - ⬜ Kandy / Lets-Plot, for loss curves (Ch 5),
  - ⬜ DJL with the PyTorch engine, for a speed comparison (Ch 6).
- ✅ Agent instructions ([AGENTS.md](../AGENTS.md)) and CI on every push.
- ✅ Formatting (Spotless + ktlint) and compiler warnings as errors (added on request). Detekt was rejected: no
  stable release supports Kotlin 2.4.

## 3. Curriculum

Cover all of it, also the parts that the Node.js book marks "Coming Soon". Section ids are in
`build-logic/src/main/kotlin/llm/conventions/Curriculum.kt`.

| Chapter | Sections | Status |
|---|---|---|
| 1 Foundations (`core`) | s1 Tensor basics (shape, strides, views); s2 element-wise ops and broadcasting; s3 matmul and reductions; s4 reverse-mode autograd; s5 Linear layer and SGD, with gradient checks | 🟡 s1 done |
| 2 Tokenizer and data | s1 word-level tokenizer; s2 byte-pair encoding (checked against JTokkit); s3 sliding-window sampling; s4 Dataset and DataLoader; s5 token and positional embeddings | ⬜ |
| 3 Attention | s1 simplified self-attention; s2 trainable self-attention (Q, K, V); s3 causal mask and dropout; s4 multi-head attention | ⬜ |
| 4 GPT model | s1 LayerNorm; s2 GELU feed-forward; s3 transformer block (pre-LN, residuals); s4 full GPT (GPT-2 small config and a tiny laptop config); s5 parameter counting | ⬜ |
| 5 Training | s1 cross-entropy; s2 AdamW; s3 training loop with train/val loss; s4 text generation (greedy, temperature, top-k); s5 saving and loading weights | ⬜ |
| 6 Bonus | s1 load pretrained GPT-2 weights (safetensors); s2 performance (coroutines/parallel matmul, Vector API); s3 DJL comparison | ⬜ |

## 4. Tutorial site (GitHub Pages)

- ✅ In `site/`, deployed by GitHub Actions to <https://mohaqeq.github.io/kotlin-llm-from-scratch/> (project-site
  base path `/kotlin-llm-from-scratch`).
- ✅ Astro + Starlight with a custom theme that looks clearly different from book.humanexperiences.io:
  theme "Lab notebook" (warm paper, deep teal), light and dark mode, custom landing page (TDD loop drawing, chapter
  map). Headings use JetBrains Mono like the owner's blog (owner's request).
  The owner has not formally chosen between the three proposed themes; "Lab notebook" is active.
- ✅ One overview page per chapter. 🟡 One page per section, written as a TDD walkthrough (template in
  [WORKFLOW.md](WORKFLOW.md)); ch01-s1 is done.
- Each section page: "Start here" box, concept, diagrams, tensor-shape tables, key code taken directly from the
  repository files (✅ `Snippet` component), "Run it", exercises, link to the source.
- ✅ "In progress" / "Planned" sidebar badges, automatic from front matter.
- ✅ Original prose in the owner's voice. ✅ An About page credits the Node.js book and Raschka's book.

## 5. Final step: blog post ⬜

When everything is done, write a post for <https://mohaqeq.github.io/> (Hugo + PaperMod theme, author
"Hamidreza Mohaqeq", URL format `/posts/YYYY-MM-slug/`).

- A Markdown file with PaperMod front matter (`title`, `date`, `tags`, `summary`, optional `cover`, `ShowToc: true`)
  in the blog repository's `content/posts/` folder.
- **Ask the owner for the blog repository path and name first.** Open a pull request; never push to its main branch.
- Content: why the owner did this, what they learned per chapter, surprising bugs, a loss-curve chart, sample
  generated text, a speed comparison (pure Kotlin vs DJL), and links to the repository and the site.
- The blog has these top-level paths: `/about/`, `/archives/`, `/categories/`, `/posts/`, `/tags/`. Never create a
  blog path named `/kotlin-llm-from-scratch/`: it would conflict with this project site.
