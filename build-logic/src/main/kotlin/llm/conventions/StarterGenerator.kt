package llm.conventions

import java.io.File

/**
 * Makes the starter state of a section from the solution code.
 *
 * The solution in `src/main/kotlin` marks the parts that a learner writes:
 *
 * ```
 * fun size(): Int {
 *     // <exercise ch01-s1> Multiply all dimensions of the shape.
 *     return shape.fold(1) { acc, d -> acc * d }
 *     // </exercise>
 * }
 * ```
 *
 * For a target section T, each `exercise` region of section T or later becomes
 * `TODO("ch01-s1: Multiply all dimensions of the shape.")`. Regions of earlier
 * sections keep the solution code. A `solution-only` region of section T or
 * later is removed completely (use it for private helpers of the solution).
 * The markers themselves never appear in the output.
 */
object StarterGenerator {
    private val begin = Regex("""^(\s*)//\s*<(exercise|solution-only)\s+(ch\d{2}-s\d+)>\s*(.*)$""")
    private val end = Regex("""^\s*//\s*</(exercise|solution-only)>\s*$""")

    fun generate(solutionDir: File, exerciseDir: File, target: SectionId) {
        exerciseDir.deleteRecursively()
        if (!solutionDir.isDirectory) return
        solutionDir.walkTopDown().filter { it.isFile }.forEach { file ->
            val out = File(exerciseDir, file.relativeTo(solutionDir).path)
            out.parentFile.mkdirs()
            if (file.extension == "kt") {
                out.writeText(transform(file.readText(), target, file.path))
            } else {
                file.copyTo(out, overwrite = true)
            }
        }
    }

    fun transform(text: String, target: SectionId, fileName: String = "<text>"): String {
        val lines = text.lines()
        val out = ArrayList<String>(lines.size)
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            val m = begin.matchEntire(line)
            if (m == null) {
                require(!end.matches(line)) { "$fileName:${i + 1}: end marker without a begin marker" }
                out += line
                i++
                continue
            }
            val (indent, kind, idText, hint) = m.destructured
            val id = SectionId.parse(idText)!!
            require(id.chapter == target.chapter) {
                "$fileName:${i + 1}: marker $id belongs to another chapter than $target"
            }
            val close = (i + 1 until lines.size).firstOrNull { j ->
                require(!begin.matches(lines[j])) { "$fileName:${j + 1}: nested exercise markers are not allowed" }
                end.matches(lines[j])
            } ?: error("$fileName:${i + 1}: no end marker for $kind $id")
            require(end.matchEntire(lines[close])!!.groupValues[1] == kind) {
                "$fileName:${close + 1}: end marker does not match <$kind>"
            }
            val body = lines.subList(i + 1, close)
            when {
                id < target -> out += body
                kind == "exercise" -> out += "${indent}TODO(\"${escape("$id: ${hint.ifBlank { "implement this" }}")}\")"
                else -> Unit // solution-only: drop it
            }
            i = close + 1
        }
        return out.joinToString("\n")
    }

    private fun escape(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("$", "\\$")
}
