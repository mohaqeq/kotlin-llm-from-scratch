// Every chapter module applies the "llm.chapter" convention plugin from build-logic/.
// See HOW-TO-LEARN.md for the exercise/solution workflow.

plugins {
    id("llm.chapter") apply false
}

tasks.register("listSections") {
    group = "learning"
    description = "Prints the id of every section that has tests (one per line)."
    val ids = llm.conventions.Curriculum.sectionsWithTests(rootDir)
    doLast { ids.forEach { println(it) } }
}
