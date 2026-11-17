package llm.conventions

import java.io.File

/** One section id, for example `ch03-s2` = chapter 3, section 2. */
data class SectionId(val chapter: Int, val section: Int) : Comparable<SectionId> {
    override fun compareTo(other: SectionId): Int =
        compareValuesBy(this, other, SectionId::chapter, SectionId::section)

    override fun toString(): String = "ch%02d-s%d".format(chapter, section)

    /** Package of the tests and of the runnable main() of this section. */
    val packageName: String get() = "llm.ch%02d.s%d".format(chapter, section)

    /** Fully qualified class name of the section's main() function. */
    val mainClass: String get() = "$packageName.Section${section}Kt"

    companion object {
        private val full = Regex("""ch(\d{1,2})-s(\d{1,2})""")
        private val short = Regex("""(\d{1,2})""")

        /**
         * Parses `ch03-s2`, or a bare section number such as `2` when [defaultChapter] is known.
         * Returns null when the text has neither form.
         */
        fun parse(text: String, defaultChapter: Int? = null): SectionId? {
            full.matchEntire(text.trim())?.let { m ->
                return SectionId(m.groupValues[1].toInt(), m.groupValues[2].toInt())
            }
            if (defaultChapter != null) {
                short.matchEntire(text.trim())?.let { m -> return SectionId(defaultChapter, m.groupValues[1].toInt()) }
            }
            return null
        }
    }
}

data class Chapter(
    val number: Int,
    val projectPath: String,
    val title: String,
    val sections: List<String>,
) {
    /** Folder of the module, relative to the repository root. */
    val dir: String get() = projectPath.removePrefix(":").replace(':', '/')

    fun section(n: Int) = SectionId(number, n)

    fun testDir(rootDir: File, n: Int): File =
        File(rootDir, "$dir/src/test/kotlin/" + section(n).packageName.replace('.', '/'))
}

/**
 * The single list of chapters and sections. The build, CI and the
 * START.md files use it. Keep it in sync with site/src/content/docs.
 */
object Curriculum {
    val chapters: List<Chapter> = listOf(
        Chapter(
            1, ":core", "Foundations",
            listOf(
                "Tensor basics",
                "Element-wise operations and broadcasting",
                "Matrix multiplication and reductions",
                "Reverse-mode autograd",
                "Linear layer and SGD",
            ),
        ),
        Chapter(
            2, ":chapters:ch02-tokenizer", "Tokenizer and data",
            listOf(
                "Word-level tokenizer",
                "Byte-pair encoding",
                "Sliding-window sampling",
                "Dataset and DataLoader",
                "Token and positional embeddings",
            ),
        ),
        Chapter(
            3, ":chapters:ch03-attention", "Attention",
            listOf(
                "Simplified self-attention",
                "Trainable self-attention",
                "Causal mask and dropout",
                "Multi-head attention",
            ),
        ),
        Chapter(
            4, ":chapters:ch04-gpt", "GPT model",
            listOf(
                "Layer normalization",
                "GELU feed-forward network",
                "Transformer block",
                "Full GPT model",
                "Parameter counting",
            ),
        ),
        Chapter(
            5, ":chapters:ch05-training", "Training",
            listOf(
                "Cross-entropy loss",
                "AdamW",
                "Training loop",
                "Text generation",
                "Saving and loading weights",
            ),
        ),
        Chapter(
            6, ":chapters:ch06-bonus", "Bonus",
            listOf(
                "Loading GPT-2 weights",
                "Performance: parallel matmul and the Vector API",
                "DJL comparison",
            ),
        ),
    )

    fun byPath(path: String): Chapter? = chapters.firstOrNull { it.projectPath == path }

    fun byNumber(n: Int): Chapter? = chapters.firstOrNull { it.number == n }

    /** Sections that have a test package. Only these sections have a starter state to verify. */
    fun sectionsWithTests(rootDir: File): List<SectionId> =
        chapters.flatMap { ch ->
            ch.sections.indices.map { it + 1 }
                .filter { ch.testDir(rootDir, it).isDirectory }
                .map { ch.section(it) }
        }
}
