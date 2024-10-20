plugins {
    id("java")
}

sourceSets {
    create("intTest") {
        compileClasspath += sourceSets.main.get().output
        compileClasspath += sourceSets.test.get().output

        runtimeClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.test.get().output
    }
}

configurations["intTestImplementation"].extendsFrom(configurations.testImplementation.get())
configurations["intTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())
configurations["intTestAnnotationProcessor"].extendsFrom(configurations.testAnnotationProcessor.get())
configurations["intTestCompileOnly"].extendsFrom(configurations.testCompileOnly.get())

val integrationTest = task<Test>("intTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    mustRunAfter("test")

    testLogging {
        events("passed")
    }
}

tasks.check { dependsOn(integrationTest) }

