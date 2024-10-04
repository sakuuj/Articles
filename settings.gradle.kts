rootProject.name = "blogsite"
include("platform")

include("services:person-service")
include("services:article-service")
include("services:elasticsearch-consumer")

include("index-creator-elasticsearch-spring-boot-starter")

include("services:common:person-service-grpc-common")
include("services:common:security-common")
include("services:common:service-common")
include("services:common:service-common-jpa")
include("services:common:service-common-elasticsearch")
