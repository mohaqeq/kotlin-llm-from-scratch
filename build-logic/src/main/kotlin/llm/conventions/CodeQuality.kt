package llm.conventions

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Formatting and compiler checks for the hand-written code of a module.
 *
 * - Spotless runs ktlint (rules come from the root `.editorconfig`).
 *   `spotlessApply` fixes the files, `spotlessCheck` (part of `check`) fails on unformatted code.
 * - Kotlin compiler warnings (for example a shadowed name or an unused variable) fail the build.
 *
 * Both apply only to `src/main` and `src/test`. `src/exercise` is generated from `src/main`, and its
 * TODO() skeletons may cause warnings on purpose.
 *
 * Outside CI, every compile task runs `spotlessApply` first, so local builds format the code
 * automatically. On CI (environment variable `CI` is set), only the check runs.
 */
internal fun Project.configureCodeQuality(libs: VersionCatalog) {
    pluginManager.apply("com.diffplug.spotless")

    val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion
    // An explicit path makes .editorconfig a task input; else the daemon keeps old rules in a cache.
    val editorConfig = rootProject.file(".editorconfig")
    extensions.configure<SpotlessExtension> {
        kotlin {
            target("src/main/kotlin/**/*.kt", "src/test/kotlin/**/*.kt")
            ktlint(ktlintVersion).setEditorConfigPath(editorConfig)
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint(ktlintVersion).setEditorConfigPath(editorConfig)
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        if (!name.contains("Exercise")) compilerOptions.allWarningsAsErrors.set(true)
    }

    if (System.getenv("CI") == null) {
        tasks.matching { (it.name.startsWith("compile") && it.name.endsWith("Kotlin")) || it.name == "resetExercise" }
            .configureEach { dependsOn("spotlessApply") }
    }
}
