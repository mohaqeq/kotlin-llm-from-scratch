# Progress

My progress through the curriculum. I update this file at the end of every session.

Legend: `[x]` done (tests green, guide written, starter branch made), `[~]` in progress, `[ ]` not started.

## Setup

- [x] Repository, Gradle multi-module build, convention plugin (exercise/solution modes)
- [x] CI: solution tests, exercise sync check, starter verification
- [x] Site skeleton (Astro + Starlight) deployed to GitHub Pages
- [ ] Choose the final site theme (A "Lab notebook" is active; B and C are options)

## Chapter 1 · Foundations (`core/`)

- [~] ch01-s1 Tensor basics
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
