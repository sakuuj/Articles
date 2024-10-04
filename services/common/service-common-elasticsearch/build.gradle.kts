plugins {
    id("java")
    id("int-test")
    id("idea")
    alias(libs.plugins.springBoot)
    alias(libs.plugins.hibernate)
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
    mavenLocal()
}

idea {
    module {
        testSources.from(sourceSets.intTest.get().allSource.srcDirs)
    }
}


dependencies {
    annotationProcessor(platform(project(":platform")))
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    implementation(platform(project(":platform")))
    implementation(project(":services:common:service-common"))
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("by.sakuuj.elasticsearch:index-creator-elasticsearch-spring-boot-starter")


    testAnnotationProcessor(platform(project(":platform")))
    testAnnotationProcessor("org.projectlombok:lombok")

    testCompileOnly("org.projectlombok:lombok")

    testImplementation("org.junit.jupiter:junit-jupiter")
    intTestImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.intTest {

    useJUnitPlatform()
}