package llm.conventions

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import java.io.File
import java.util.Properties

/**
 * Convention plugin for every chapter module (`:core` is chapter 1).
 *
 * Two source folders exist in each module:
 * - `src/main/kotlin`     the solution,
 * - `src/exercise/kotlin` the learner's code (starts as skeletons with TODO()).
 *
 * The mode decides which folder is the Gradle `main` source set, so the tests
 * and `run` use it. Other modules always use the *solution* of earlier
 * chapters (configuration `solutionElements`), never the learner's code.
 */
class ChapterPlugin : Plugin<Project> {

    enum class Mode { SOLUTION, EXERCISE }

    override fun apply(project: Project): Unit = with(project) {
        val chapter = Curriculum.byPath(path)
            ?: throw GradleException("Project $path is not in build-logic/.../Curriculum.kt")

        pluginManager.apply("org.jetbrains.kotlin.jvm")
        pluginManager.apply("application")
        repositories.mavenCentral()

        val taskNames = gradle.startParameter.taskNames.map { it.substringAfterLast(':') }
        val mode = resolveMode(taskNames)
        val requested = providers.gradleProperty("section").orNull?.let {
            SectionId.parse(it, chapter.number)
                ?: throw GradleException("Bad -Psection=$it. Use a form like ch03-s2 (or 2 inside one module).")
        }
        if (requested == null && taskNames.any { it == "startSection" || it == "verifyExercise" }) {
            throw GradleException("Give the section, for example: ./gradlew startSection -Psection=ch01-s1")
        }
        if (requested != null && requested.chapter == chapter.number && requested.section !in 1..chapter.sections.size) {
            throw GradleException("Chapter ${chapter.number} has sections 1..${chapter.sections.size}, not $requested")
        }
        val isTarget = requested == null || requested.chapter == chapter.number

        // Each mode has its own build folder. Both modes compile files with the same relative
        // paths (llm/core/Tensor.kt) in the same task, so a shared folder lets Kotlin incremental
        // compilation keep the class files of the other mode.
        if (mode == Mode.EXERCISE) layout.buildDirectory.set(layout.projectDirectory.dir("build/exercise-mode"))

        val kotlin = extensions.getByType<KotlinJvmProjectExtension>()
        kotlin.jvmToolchain(25)

        // --- Source sets: which folder is "main" depends on the mode -------------
        val sourceSets = extensions.getByType<SourceSetContainer>()
        val solutionDir = "src/main/kotlin"
        val exerciseDir = "src/exercise/kotlin"
        val otherName = if (mode == Mode.SOLUTION) "exercise" else "solution"
        kotlin.sourceSets.getByName("main").kotlin.setSrcDirs(
            listOf(if (mode == Mode.SOLUTION) solutionDir else exerciseDir),
        )
        val other = sourceSets.create(otherName)
        other.resources.setSrcDirs(emptyList<File>())
        kotlin.sourceSets.getByName(otherName).kotlin.setSrcDirs(
            listOf(if (mode == Mode.SOLUTION) exerciseDir else solutionDir),
        )
        configurations.getByName("${otherName}Implementation").extendsFrom(configurations.getByName("implementation"))
        configurations.getByName("${otherName}RuntimeOnly").extendsFrom(configurations.getByName("runtimeOnly"))
        // "check" always compiles both folders: the exercise code must compile too.
        tasks.named("check") { dependsOn(other.classesTaskName) }

        // --- Export the solution to later chapters ------------------------------
        val solutionSourceSet = if (mode == Mode.SOLUTION) sourceSets.getByName("main") else other
        val solutionJar = tasks.register<Jar>("solutionJar") {
            archiveClassifier.set("solution")
            from(solutionSourceSet.output)
        }
        configurations.create("solutionElements") {
            isCanBeConsumed = true
            isCanBeResolved = false
            extendsFrom(configurations.getByName("implementation"), configurations.getByName("runtimeOnly"))
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
                attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements::class.java, LibraryElements.JAR))
                attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling::class.java, Bundling.EXTERNAL))
            }
            outgoing.artifact(solutionJar)
        }

        // --- Dependencies -------------------------------------------------------
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies.apply {
            Curriculum.chapters.filter { it.number < chapter.number }.forEach { earlier ->
                add("implementation", project(mapOf("path" to earlier.projectPath, "configuration" to "solutionElements")))
            }
            listOf("kotest-runner", "kotest-assertions", "kotest-property").forEach {
                add("testImplementation", libs.findLibrary(it).get())
            }
        }

        // --- Tests --------------------------------------------------------------
        val verifying = "verifyExercise" in taskNames
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            enabled = isTarget
            requested?.let { filter.includeTestsMatching("${it.packageName}.*") }
            ignoreFailures = verifying
            testLogging {
                events("passed", "failed", "skipped")
                exceptionFormat = TestExceptionFormat.SHORT
            }
        }

        // --- run -Psection=N ----------------------------------------------------
        val runSection = requested?.takeIf { it.chapter == chapter.number } ?: chapter.section(1)
        extensions.getByType<JavaApplication>().apply {
            mainClass.set(runSection.mainClass)
            applicationDefaultJvmArgs = listOf("-Xmx4g")
        }
        tasks.named<JavaExec>("run") {
            enabled = isTarget
            workingDir = rootDir // so code can read data/ with a relative path
        }

        // --- Learning tasks -----------------------------------------------------
        val target = requested?.takeIf { it.chapter == chapter.number } ?: chapter.section(1)
        val solutionFolder = file(solutionDir)
        val exerciseFolder = file(exerciseDir)
        val reset = tasks.register("resetExercise") {
            group = "learning"
            description = "Resets src/exercise to the starter state of -Psection (default: the first section)."
            enabled = isTarget
            doLast {
                StarterGenerator.generate(solutionFolder, exerciseFolder, target)
                logger.lifecycle("Reset ${exerciseFolder.relativeTo(rootDir).invariantSeparatorsPath} to the starter state of $target.")
            }
        }
        tasks.matching { it.name.startsWith("compile") || it.name == "test" }.configureEach { mustRunAfter(reset) }

        tasks.register("startSection") {
            group = "learning"
            description = "Resets the exercise of -Psection and runs its tests (they fail: RED)."
            enabled = requested?.chapter == chapter.number
            if (enabled) dependsOn(reset, "test")
        }

        tasks.register("verifyExercise") {
            group = "verification"
            description = "Checks that the starter state of -Psection compiles and fails only with TODO()."
            enabled = requested?.chapter == chapter.number
            if (enabled) dependsOn(reset, "test")
            val results = layout.buildDirectory.dir("test-results/test")
            doLast { ExerciseVerifier.verify(results.get().asFile, requested!!) }
        }
    }

    /**
     * Mode order: `startSection`/`verifyExercise` always use EXERCISE; then `-Pmode`;
     * then `mode=` in a git-ignored `local.properties`; else SOLUTION.
     */
    private fun Project.resolveMode(taskNames: List<String>): Mode {
        if (taskNames.any { it == "startSection" || it == "verifyExercise" }) return Mode.EXERCISE
        val local = File(rootDir, "local.properties").takeIf { it.isFile }?.let { f ->
            Properties().apply { f.reader().use { load(it) } }.getProperty("mode")
        }
        return when (val value = (providers.gradleProperty("mode").orNull ?: local)?.trim()?.lowercase()) {
            null, "", "solution" -> Mode.SOLUTION
            "exercise" -> Mode.EXERCISE
            else -> throw GradleException("Unknown mode '$value'. Use -Pmode=exercise or -Pmode=solution.")
        }
    }
}
