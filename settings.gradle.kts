pluginManagement {
    includeBuild("build-logic")
}

plugins {
    // Downloads JDK 25 automatically when it is not installed.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "kotlin-llm-from-scratch"

include(
    ":core",
    ":chapters:ch02-tokenizer",
    ":chapters:ch03-attention",
    ":chapters:ch04-gpt",
    ":chapters:ch05-training",
    ":chapters:ch06-bonus",
)
