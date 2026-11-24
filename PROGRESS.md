# Progress

My progress through the curriculum. I update this file at the end of every session.
Agents: read [AGENTS.md](AGENTS.md) for the rules and [docs/WORKFLOW.md](docs/WORKFLOW.md) for the procedures.

## Current state and next action

- **Section:** ch01-s1 Tensor basics. All 14 TDD steps are green and pushed (tags `ch01-s1-step1-red` …
  `ch01-s1-step14-green`). Property tests (`TensorPropertiesTest`) are pushed. 20 tests pass.
- **Local, not pushed:** nothing.
- **Next action:** close ch01-s1 (docs/WORKFLOW.md, section 3):
  1. Finish `core/sections/ch01-s1/START.md` (remove the "in progress" line; check "Done when").
  2. Make `Section1.kt` show the views (reshape, transpose, contiguous, toString).
  3. Build the `Snippet` site component, then write `site/src/content/docs/ch01/s1-tensor-basics.mdx` as a TDD
     walkthrough of the 14 steps; set `status: done`.
  4. Create the branch `start/ch01-s1`.
  5. Mark ch01-s1 `[x]` below, then start ch01-s2 (element-wise operations and broadcasting).
- **Open review points for the owner:** none.

Legend: `[x]` done (tests green, guide written, starter branch made), `[~]` in progress, `[ ]` not started.

## Setup

- [x] Repository, Gradle multi-module build, convention plugin (exercise/solution modes)
- [x] CI: solution tests, exercise sync check, starter verification
- [x] Site skeleton (Astro + Starlight) deployed to GitHub Pages
- [x] Formatting (Spotless + ktlint) and compiler warnings as errors
- [x] Agent-neutral docs: AGENTS.md, docs/PROJECT-BRIEF.md, docs/WORKFLOW.md, docs/DECISIONS.md
- [ ] Choose the final site theme ("Lab notebook" is active, with JetBrains Mono headings as on the blog; the other
      proposals were "Blueprint" and "Signal")

## Chapter 1 · Foundations (`core/`)

- [~] ch01-s1 Tensor basics (all steps green; closing tasks open: START.md, demo, guide page, starter branch)
- [ ] ch01-s2 Element-wise operations and broadcasting
- [ ] ch01-s3 Matrix multiplication and reductions
- [ ] ch01-s4 Reverse-mode autograd
- [ ] ch01-s5 Linear layer and SGD

## Chapter 2 · Tokenizer and data (`chapters/ch02-tokenizer/`)

- [ ] ch02-s1 Word-level tokenizer
- [ ] ch02-s2 Byte-pair encoding (checked against JTokkit)
- [ ] ch02-s3 Sliding-window sampling
- [ ] ch02-s4 Dataset and DataLoader
- [ ] ch02-s5 Token and positional embeddings

## Chapter 3 · Attention (`chapters/ch03-attention/`)

- [ ] ch03-s1 Simplified self-attention
- [ ] ch03-s2 Trainable self-attention
- [ ] ch03-s3 Causal mask and dropout
- [ ] ch03-s4 Multi-head attention

## Chapter 4 · GPT model (`chapters/ch04-gpt/`)

- [ ] ch04-s1 Layer normalization
- [ ] ch04-s2 GELU feed-forward network
- [ ] ch04-s3 Transformer block
- [ ] ch04-s4 Full GPT model
- [ ] ch04-s5 Parameter counting

## Chapter 5 · Training (`chapters/ch05-training/`)

- [ ] ch05-s1 Cross-entropy loss
- [ ] ch05-s2 AdamW
- [ ] ch05-s3 Training loop
- [ ] ch05-s4 Text generation
- [ ] ch05-s5 Saving and loading weights

## Chapter 6 · Bonus (`chapters/ch06-bonus/`)

- [ ] ch06-s1 Loading GPT-2 weights
- [ ] ch06-s2 Performance: parallel matmul and the Vector API
- [ ] ch06-s3 DJL comparison

## Final

- [ ] Blog post on mohaqeq.github.io (PR to the blog repo)

## Session log

### 2026-11-17

- Set up the repository, the build, CI and the site.
- Started ch01-s1. Step 1 is RED: `TensorBasicsTest > size is the product of all dimensions`.
- Next: I implement `Tensor.size` in `core/src/main/kotlin/llm/core/Tensor.kt`.

### 2026-11-18 to 2026-11-19

- ch01-s1 steps 1-9 green: size, scalar, length check, negative dimensions, overflow, strides, get, index checks, set, zeros/ones.
- Build fix: `main` is always the solution; exercise mode adds an `exerciseTest` source set (no task changes sources with the mode).
- Next: ch01-s1 views: reshape (shares data), transpose (swaps strides), contiguous, toString, property tests.

### 2026-11-19 to 2026-11-24

- ch01-s1 steps 10-14 green: reshape as a view, strides as a constructor parameter, transpose, contiguous, reshape of
  non-contiguous views, toString. Property tests added.
- The owner asked for the solutions of step 14 (toString) and of the property tests; the agent wrote them.
- Tooling: Spotless + ktlint (auto-format on local compile), compiler warnings as errors; detekt rejected (no stable
  release for Kotlin 2.4).
- Site: headings in JetBrains Mono, as on the owner's blog.
- Docs for any coding agent: AGENTS.md (rules), docs/ (brief, workflow, decisions).
- Next: close ch01-s1 (see "Current state and next action").
