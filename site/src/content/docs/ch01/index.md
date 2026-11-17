---
title: Foundations
description: A tensor class, a small reverse-mode autograd, a Linear layer and SGD.
status: in-progress
sidebar:
  label: Overview
  order: 0
---

In this chapter I build the numeric base of the model. All later chapters use it.
The code is in the module `core`.

| Section | Topic                                          |
| ------- | ---------------------------------------------- |
| 1       | Tensor basics: shape, strides, indexing, views |
| 2       | Element-wise operations and broadcasting       |
| 3       | Matrix multiplication and reductions           |
| 4       | Reverse-mode autograd                          |
| 5       | Linear layer and SGD                           |

Every operation with a backward pass gets a numerical gradient check.
