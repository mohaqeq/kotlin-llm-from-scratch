plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.spotless.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("chapter") {
            id = "llm.chapter"
            implementationClass = "llm.conventions.ChapterPlugin"
        }
    }
}
