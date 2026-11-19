package llm.conventions

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
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
 * The mode decides which folder the tests and `run` use. `main` is always the
 * solution, so other modules always use the solution of earlier chapters,
 * never the learner's code.
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
        if (requested != null && requested.chapter == chapter.number &&
            requested.section !in 1..chapter.sections.size
        ) {
            throw GradleException("Chapter ${chapter.number} has sections 1..${chapter.sections.size}, not $requested")
        }
        val isTarget = requested == null || requested.chapter == chapter.number

        val kotlin = extensions.getByType<KotlinJvmProjectExtension>()
        kotlin.jvmToolchain(25)

        // --- Source sets ---------------------------------------------------------
        // No task changes its sources with the mode. Gradle keeps one execution history per
        // task; a task with mode-dependent sources deletes the other mode's outputs.
        //   main          = src/main/kotlin      (solution; other chapters use it)
        //   exercise      = src/exercise/kotlin  (learner; always compiled by "check")
        //   exerciseTest  = src/test/kotlin compiled against exercise (exercise mode only)
        val sourceSets = extensions.getByType<SourceSetContainer>()
        val solutionDir = "src/main/kotlin"
        val exerciseDir = "src/exercise/kotlin"
        val exercise = sourceSets.create("exercise")
        exercise.resources.setSrcDirs(emptyList<File>())
        kotlin.sourceSets.getByName("exercise").kotlin.setSrcDirs(listOf(exerciseDir))
        configurations.getByName("exerciseImplementation").extendsFrom(configurations.getByName("implementation"))
        configurations.getByName("exerciseRuntimeOnly").extendsFrom(configurations.getByName("runtimeOnly"))
        tasks.named("check") { dependsOn(exercise.classesTaskName) }

        // --- Dependencies: the solution (main) of every earlier chapter ------------
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        configureCodeQuality(libs)
        dependencies.apply {
            Curriculum.chapters.filter { it.number < chapter.number }.forEach { earlier ->
                add("implementation", project(earlier.projectPath))
            }
            listOf("kotest-runner", "kotest-assertions", "kotest-property").forEach {
                add("testImplementation", libs.findLibrary(it).get())
            }
        }

        // In exercise mode, the tests compile against src/exercise. src/test moves from the
        // "test" source set to "exerciseTest", so no folder is in two source sets (IDE import).
        val exerciseTest = if (mode == Mode.EXERCISE) {
            kotlin.sourceSets.getByName("test").kotlin.setSrcDirs(emptyList<File>())
            sourceSets.create("exerciseTest").also { st ->
                st.resources.setSrcDirs(listOf("src/test/resources"))
                sourceSets.getByName("test").resources.setSrcDirs(emptyList<File>())
                kotlin.sourceSets.getByName("exerciseTest").kotlin.setSrcDirs(listOf("src/test/kotlin"))
                for (kind in listOf("Implementation", "RuntimeOnly")) {
                    configurations.getByName("exerciseTest$kind").extendsFrom(configurations.getByName("test$kind"))
                }
                // Tests may use `internal` members, as with main.
                kotlin.target.compilations.getByName("exerciseTest")
                    .associateWith(kotlin.target.compilations.getByName("exercise"))
            }
        } else {
            null
        }

        // --- Tests --------------------------------------------------------------
        val verifying = "verifyExercise" in taskNames
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            enabled = isTarget
            if (exerciseTest != null) {
                testClassesDirs = exerciseTest.output.classesDirs
                classpath = exerciseTest.runtimeClasspath
            }
            requested?.let { filter.includeTestsMatching("${it.packageName}.*") }
            ignoreFailures = verifying
            // The verifier must read results of a fresh run, never old ones.
            if (verifying) outputs.upToDateWhen { false }
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
            if (mode == Mode.EXERCISE) classpath = exercise.runtimeClasspath
        }

        // --- Learning tasks -----------------------------------------------------
        val target = requested?.takeIf { it.chapter == chapter.number } ?: chapter.section(1)
        val solutionFolder = file(solutionDir)
        val exerciseFolder = file(exerciseDir)
        val exerciseFolderName = exerciseFolder.relativeTo(rootDir).invariantSeparatorsPath
        val reset = tasks.register("resetExercise") {
            group = "learning"
            description = "Resets src/exercise to the starter state of -Psection (default: the first section)."
            enabled = isTarget
            doLast {
                StarterGenerator.generate(solutionFolder, exerciseFolder, target)
                logger.lifecycle("Reset $exerciseFolderName to the starter state of $target.")
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
