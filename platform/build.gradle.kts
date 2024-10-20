plugins {
    id("java-platform")
    id("maven-publish")
}

javaPlatform {
    allowDependencies()
}

group = "by.sakuuj.articles"
version = "0.1"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])
        }
    }
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:3.3.1"))
    api(platform("org.springframework.cloud:spring-cloud-dependencies:2023.0.3"))
    api(platform("io.grpc:grpc-bom:1.54.1"))

    constraints {
        api(project(":index-creator-elasticsearch-spring-boot-starter"))

        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

        api("org.projectlombok:lombok-mapstruct-binding:0.2.0")

        val mapstructVersion = "1.5.5.Final"
        api("org.mapstruct:mapstruct:$mapstructVersion")
        api("org.mapstruct:mapstruct-processor:$mapstructVersion")

        // used by java grpc client
        api("org.apache.tomcat:annotations-api:6.0.53")

        val temporalVersion = "1.25.1"
        api("io.temporal:temporal-sdk:$temporalVersion")
        api("io.temporal:temporal-testing:$temporalVersion")
    }
}