package llm.core

/**
 * Runs the demo of one section. When the code still has a TODO(), it prints
 * what is missing and how to start, and does not crash.
 */
fun runSection(id: String, title: String, body: () -> Unit) {
    println("== $id: $title ==")
    try {
        body()
    } catch (e: NotImplementedError) {
        println("TODO: " + (e.message ?: "").removePrefix("An operation is not implemented: "))
        println("Make the tests green first: ./gradlew startSection -Psection=$id")
    }
}
