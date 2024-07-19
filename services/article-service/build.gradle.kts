plugins {
    id("java")
    id("int-test")
    alias(libs.plugins.springBoot)
}

group = "by.sakuuj.blogplatform"
version = "0.1"

repositories {
    mavenCentral()
}

sourceSets.forEach { s -> println(s.java.srcDirs) }

dependencies {
    annotationProcessor(platform(project(":platform")))
    implementation(platform(project(":platform")))
    testAnnotationProcessor(platform(project(":platform")))

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")

    compileOnly("org.projectlombok:lombok")

    implementation("org.mapstruct:mapstruct")
    implementation("by.sakuuj.blogplatform:concurrency-utils")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    runtimeOnly("org.postgresql:postgresql")

    testAnnotationProcessor("org.projectlombok:lombok")

    testCompileOnly("org.projectlombok:lombok")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    intTestImplementation("org.testcontainers:postgresql")
    intTestImplementation("org.springframework.boot:spring-boot-testcontainers")
}


tasks.intTest {
    val hardwareThreadCount : String = "8"
    systemProperty("jdk.virtualThreadScheduler.maxPoolSize", hardwareThreadCount)
}

tasks.test {
    useJUnitPlatform()
}
