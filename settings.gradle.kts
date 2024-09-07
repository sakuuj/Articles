rootProject.name = "blogsite"
include("platform")

include("services:article-service")
include("services:person-service")


include("services:common:service-common")
include("services:common:int-test-common")
include("services:common:security-common")
include("services:common:person-service-grpc-common")

include("index-creator-elasticsearch-spring-boot-starter")
