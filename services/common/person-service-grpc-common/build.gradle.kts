import com.google.protobuf.gradle.*

plugins {
    id("java-library")
    id("com.google.protobuf") version "0.9.4"
}

group = "by.sakuuj.blogsite"
version = "0.1"

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.66.0"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {}
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(project(":platform")))

    api("io.grpc:grpc-protobuf")
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-netty-shaded")
    api("org.apache.tomcat:annotations-api")
}




