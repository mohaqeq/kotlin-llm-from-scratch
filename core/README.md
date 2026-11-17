# core · Chapter 1: Foundations

The numeric base of the model, in package `llm.core`: a tensor with shapes and strides, element-wise operations with
broadcasting, matrix multiplication, a reverse-mode autograd, a Linear layer and SGD. All later chapters use it.

Guide: <https://mohaqeq.github.io/kotlin-llm-from-scratch/ch01/>

| Section | Topic                                    | Start                                                 |
| ------- | ---------------------------------------- | ----------------------------------------------------- |
| ch01-s1 | Tensor basics                            | [sections/ch01-s1/START.md](sections/ch01-s1/START.md) |
| ch01-s2 | Element-wise operations and broadcasting | planned                                               |
| ch01-s3 | Matrix multiplication and reductions     | planned                                               |
| ch01-s4 | Reverse-mode autograd                    | planned                                               |
| ch01-s5 | Linear layer and SGD                     | planned                                               |

```sh
./gradlew :core:test                                   # solution, all sections
./gradlew :core:test -Pmode=exercise -Psection=ch01-s1 # your code, one section
./gradlew :core:run -Psection=1                        # demo of section 1
```
