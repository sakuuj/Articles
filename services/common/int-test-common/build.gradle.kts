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
//    annotationProcessor("org.mapstruct:mapstruct-processor")
//    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen")
//    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")

//    compileOnly("org.mapstruct:mapstruct")
    compileOnly("org.projectlombok:lombok")

//    implementation(project(":concurrency-utils"))
    implementation(platform(project(":platform")))
//    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
//    implementation("org.springframework.boot:spring-security-oauth2-jose")

//    implementation(project(":services:commons"))
//    implementation("org.liquibase:liquibase-core")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.junit.jupiter:junit-jupiter")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.testcontainers:junit-jupiter")

}
