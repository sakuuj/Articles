plugins {
    id("java-platform")
    id("maven-publish")
}

javaPlatform {
    allowDependencies()
}

group = "by.sakuuj.blogsite"
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

    constraints {
        api(project(":index-creator-elasticsearch-spring-boot-starter"))

        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

        api("org.projectlombok:lombok-mapstruct-binding:0.2.0")

        val mapstructVersion = "1.5.5.Final"
        api("org.mapstruct:mapstruct:$mapstructVersion")
        api("org.mapstruct:mapstruct-processor:$mapstructVersion")

        val grpcVersion = "1.66.0"
        api("io.grpc:grpc-stub:$grpcVersion")
        api("io.grpc:grpc-protobuf:$grpcVersion")
        api("io.grpc:grpc-netty-shaded:$grpcVersion")
        api("org.apache.tomcat:annotations-api:6.0.53")
    }
}