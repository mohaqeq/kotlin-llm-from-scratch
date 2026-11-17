plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("chapter") {
            id = "llm.chapter"
            implementationClass = "llm.conventions.ChapterPlugin"
        }
    }
}
