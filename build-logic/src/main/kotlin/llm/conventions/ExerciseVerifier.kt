package llm.conventions

import org.gradle.api.GradleException
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Reads the JUnit XML results of a test run on a starter state and checks the rules:
 *
 * - Tests of earlier sections pass (the starter has their solution).
 * - Tests of the target section fail, and only with NotImplementedError (from TODO()).
 *   At least one of them fails, so the exercise is not solved already.
 * - Tests of later sections pass or fail with NotImplementedError.
 */
object ExerciseVerifier {
    private const val TODO_ERROR = "kotlin.NotImplementedError"
    private val sectionOfClass = Regex("""^llm\.ch(\d{2})\.s(\d+)\.""")

    private data class Case(val className: String, val name: String, val failureType: String?, val skipped: Boolean)

    fun verify(resultsDir: File, target: SectionId) {
        val cases = readCases(resultsDir)
        if (cases.isEmpty()) throw GradleException("No test results in $resultsDir for $target")

        val problems = mutableListOf<String>()
        var targetFailures = 0
        for (c in cases.filterNot { it.skipped }) {
            val m = sectionOfClass.find(c.className) ?: continue
            val section = SectionId(m.groupValues[1].toInt(), m.groupValues[2].toInt())
            val label = "${c.className} > ${c.name}"
            when {
                section < target && c.failureType != null ->
                    problems += "$label: earlier section must pass in the starter, but failed with ${c.failureType}"
                section == target && c.failureType != null && c.failureType != TODO_ERROR ->
                    problems += "$label: must fail only with $TODO_ERROR, but failed with ${c.failureType}"
                section > target && c.failureType != null && c.failureType != TODO_ERROR ->
                    problems += "$label: later section failed with ${c.failureType}"
            }
            if (section == target && c.failureType == TODO_ERROR) targetFailures++
        }
        if (targetFailures == 0) problems += "No test of $target fails: the starter state looks solved already."
        if (problems.isNotEmpty()) {
            throw GradleException("Starter state of $target is wrong:\n" + problems.joinToString("\n") { "  - $it" })
        }
        println("OK: starter state of $target compiles, earlier sections pass, $targetFailures test(s) fail with TODO().")
    }

    private fun readCases(dir: File): List<Case> {
        val factory = DocumentBuilderFactory.newInstance()
        return dir.listFiles { f -> f.name.startsWith("TEST-") && f.name.endsWith(".xml") }.orEmpty().flatMap { file ->
            val doc = factory.newDocumentBuilder().parse(file)
            val nodes = doc.getElementsByTagName("testcase")
            (0 until nodes.length).map { idx ->
                val el = nodes.item(idx) as org.w3c.dom.Element
                val failure = (el.getElementsByTagName("failure").item(0)
                    ?: el.getElementsByTagName("error").item(0)) as org.w3c.dom.Element?
                Case(
                    className = el.getAttribute("classname"),
                    name = el.getAttribute("name"),
                    failureType = failure?.getAttribute("type")?.ifBlank { "unknown" },
                    skipped = el.getElementsByTagName("skipped").length > 0,
                )
            }
        }
    }
}
