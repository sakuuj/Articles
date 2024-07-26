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

dependencies {
    annotationProcessor(platform(project(":platform")))
    implementation(platform(project(":platform")))
    testAnnotationProcessor(platform(project(":platform")))

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    compileOnly("org.projectlombok:lombok")
    implementation("org.mapstruct:mapstruct")
    implementation(project(":concurrency-utils"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    runtimeOnly("org.postgresql:postgresql")

    testAnnotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    intTestImplementation("org.testcontainers:postgresql")
    intTestImplementation("org.springframework.boot:spring-boot-testcontainers")
}

val customSystemProps = mapOf(
    "jdk.virtualThreadScheduler.maxPoolSize" to "8",
    "jdk.tracePinnedThreads" to "full"
)

tasks.bootRun {
    systemProperties(customSystemProps)
}

tasks.test {
    systemProperties(customSystemProps)

    useJUnitPlatform()
}

tasks.intTest {
    systemProperties(customSystemProps)
}
