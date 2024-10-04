
plugins {
    id("java")
    id("int-test")
    id("idea")
    alias(libs.plugins.springBoot)
    alias(libs.plugins.hibernate)
}




group = "by.sakuuj.blogsite"
version = "0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {

    annotationProcessor(platform(project(":platform")))
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen")

    compileOnly("org.mapstruct:mapstruct")
    compileOnly("org.projectlombok:lombok")

    implementation(platform(project(":platform")))
    implementation(project(":services:common:person-service-grpc-common"))
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation(project(":services:common:service-common-jpa"))
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    runtimeOnly("org.postgresql:postgresql")


    testAnnotationProcessor(platform(project(":platform")))
    testAnnotationProcessor("org.projectlombok:lombok")

    testCompileOnly("org.mapstruct:mapstruct")
    testCompileOnly("org.projectlombok:lombok")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    intTestImplementation("com.h2database:h2")
    intTestImplementation("org.testcontainers:postgresql")
    intTestImplementation("org.testcontainers:junit-jupiter")
    intTestImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
}

// mark intTest directories as Test Sources and Test Resources
idea {
    module {
        testSources.from(sourceSets.intTest.get().allSource.srcDirs)
    }
}

val CUSTOM_SYSTEM_PROPS = mapOf(
    "jdk.virtualThreadScheduler.maxPoolSize" to "8",
    "jdk.tracePinnedThreads" to "full"
)

val SPRING_PROFILES_ACTIVE = "spring.profiles.active"

tasks.bootRun {

    // setting workingDir as rootDir in order for 'configtree:' to work correctly
    // (configtree does not read config files that have '..' in their path specified,
    // see ConfigTreePropertySource#findAll(Path, Set<Options>), that uses Files.find(...)
    // with a predicate PropertyFile::isPropertyFile, which returns false if a path has '..')
    setWorkingDir("$rootDir")

    systemProperties(CUSTOM_SYSTEM_PROPS)
    systemProperty(SPRING_PROFILES_ACTIVE, "prod")
}

tasks.test {
    systemProperties(CUSTOM_SYSTEM_PROPS)
    systemProperty(SPRING_PROFILES_ACTIVE, "test")

    useJUnitPlatform()
}

tasks.intTest {
    systemProperties(CUSTOM_SYSTEM_PROPS)
    systemProperty(SPRING_PROFILES_ACTIVE, "intTest")

    useJUnitPlatform()
}
