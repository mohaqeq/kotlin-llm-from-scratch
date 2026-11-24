# Kotlin LLM from Scratch

[![CI](https://github.com/mohaqeq/kotlin-llm-from-scratch/actions/workflows/ci.yml/badge.svg)](https://github.com/mohaqeq/kotlin-llm-from-scratch/actions/workflows/ci.yml)
[![Site](https://github.com/mohaqeq/kotlin-llm-from-scratch/actions/workflows/site.yml/badge.svg)](https://mohaqeq.github.io/kotlin-llm-from-scratch/)

Build a GPT-style language model in pure Kotlin, one failing test at a time.

- No Python and no deep-learning framework. The tensor, the autograd, the layers and the optimizer are plain Kotlin.
- Every step is test-driven: a failing test first, then the minimum code, then a refactor.
- You can start at any section. Each section has a starter state that contains the solution of all earlier sections.

**Tutorial site:** <https://mohaqeq.github.io/kotlin-llm-from-scratch/>

## Quick start

```sh
git clone https://github.com/mohaqeq/kotlin-llm-from-scratch.git
cd kotlin-llm-from-scratch
./gradlew startSection -Psection=ch01-s1
```

Then read [HOW-TO-LEARN.md](HOW-TO-LEARN.md).

## Chapters

| Chapter | Module                     | Topics                                                                  |
| ------- | -------------------------- | ----------------------------------------------------------------------- |
| 1       | `core/`                    | Tensor, broadcasting, matmul, reverse-mode autograd, Linear, SGD        |
| 2       | `chapters/ch02-tokenizer/` | Word tokenizer, byte-pair encoding, sliding windows, DataLoader, embeddings |
| 3       | `chapters/ch03-attention/` | Self-attention, Q/K/V, causal mask, dropout, multi-head attention       |
| 4       | `chapters/ch04-gpt/`       | LayerNorm, GELU, transformer block, GPT model, parameter count          |
| 5       | `chapters/ch05-training/`  | Cross-entropy, AdamW, training loop, text generation, checkpoints       |
| 6       | `chapters/ch06-bonus/`     | Pretrained GPT-2 weights, DJL comparison, performance                   |

Progress: see [PROGRESS.md](PROGRESS.md).

## Contributors and coding agents

- [AGENTS.md](AGENTS.md): the rules for any coding agent (Claude Code, Codex, Cursor, Copilot, Gemini) and for people.
- [PROGRESS.md](PROGRESS.md): the current state and the next action.
- [docs/](docs/): the [project brief](docs/PROJECT-BRIEF.md), the [workflow](docs/WORKFLOW.md), and the
  [decisions](docs/DECISIONS.md).
- [CONTRIBUTING.md](CONTRIBUTING.md): exercise markers and formatting.

## Requirements

- JDK 25 (Gradle downloads it if it is not installed).
- Node.js 24 only if you want to build the site locally (`cd site && npm ci && npm run dev`).

## Credits

This project is inspired by Sebastian Raschka's book _Build a Large Language Model (From Scratch)_
and by [book.humanexperiences.io](https://book.humanexperiences.io/). All text and code here are original.

## License

[MIT](LICENSE)
