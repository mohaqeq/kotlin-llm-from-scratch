---
title: Tokenizer and data
description: From raw text to batches of token ids and their embeddings.
status: planned
sidebar:
  label: Overview
  order: 0
---

This chapter changes text into numbers. It starts with a word-level tokenizer. Then it builds byte-pair encoding and compares it with the GPT-2 tokenizer. Then it cuts the token stream into training samples with a sliding window. At the end, it adds token embeddings and positional embeddings.
