// Every chapter module applies the "llm.chapter" convention plugin from build-logic/.
// See HOW-TO-LEARN.md for the exercise/solution workflow.

import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("llm.chapter") apply false
}

// Formatting of the build itself (the chapter modules format their own code).
repositories { mavenCentral() } // Spotless downloads ktlint from here
apply(plugin = "com.diffplug.spotless")
configure<SpotlessExtension> {
    val ktlintVersion = libs.versions.ktlint.get()
    val editorConfig = file(".editorconfig")
    kotlin {
        target("build-logic/src/**/*.kt")
        ktlint(ktlintVersion).setEditorConfigPath(editorConfig)
    }
    kotlinGradle {
        target("*.gradle.kts", "build-logic/*.gradle.kts")
        ktlint(ktlintVersion).setEditorConfigPath(editorConfig)
    }
}

tasks.register("listSections") {
    group = "learning"
    description = "Prints the id of every section that has tests (one per line)."
    val ids = llm.conventions.Curriculum.sectionsWithTests(rootDir)
    doLast { ids.forEach { println(it) } }
}
