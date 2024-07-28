plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

group = "by.sakuuj.blogplatform"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:3.3.1"))
    api(platform("org.springframework.cloud:spring-cloud-dependencies:2023.0.3"))

    constraints {
        api(project(":concurrency-utils"))

        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

        api("org.projectlombok:lombok-mapstruct-binding:0.2.0")

        val mapstructVersion = "1.5.5.Final"
        api("org.mapstruct:mapstruct:$mapstructVersion")
        api("org.mapstruct:mapstruct-processor:$mapstructVersion")
    }
}