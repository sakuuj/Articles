rootProject.name = "blogsite"
include("platform")

include("services:article-service")

include("services:common")
include("services:int-test-common")

include("concurrency-utils")
include("index-creator-elasticsearch-spring-boot-starter")
include("services:person-service")
include("services:person-service-grpc-common")
