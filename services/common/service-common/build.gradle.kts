plugins {
    id("java")
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor(platform(project(":platform")))
    annotationProcessor("org.projectlombok:lombok")

    compileOnly("org.projectlombok:lombok")
    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation(platform(project(":platform")))
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")


    testAnnotationProcessor(platform(project(":platform")))
    testAnnotationProcessor("org.projectlombok:lombok")

    testCompileOnly("org.projectlombok:lombok")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

}

tasks.test {
    useJUnitPlatform()
}