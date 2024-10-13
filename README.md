# articles

---

Back-end web-приложение, содержит статьи различных тематик. Можно проводить поиск статей по тематикам, ключевым словам
имеющимся в заголовках и внутри самих статей (ключевые слова могут быть написаны с ошибками).
Изменения, относящиеся к статьям, транслируются через Kafka в Elasticsearch. Для обеспечения гарантий согласованности 
при потенциальных сбоях java-приложения, используется оркестратор микросервисов Temporal (приложение может оказаться в 
несогласованном состоянии, если не используется оркестратор и сбой сервера произошел когда изменение статьи 
уже зафиксировано в базе данных, но еще не отправлено на Kafka-топик).
Для получения доступа к статьям и для их поиска по ключевым словам аутентификация не требуется.
Для публикации статей требуется аутентификация.
Регистрация и аутентификация в приложении осуществляется через аутентификацию в Google (OIDC). 

Используемые технологии:
- Java 21 & Spring Boot
- Elasticsearch
- PostgreSQL
- Apache Kafka
- Temporal

---

Используемые фреймворки/библиотеки:
- Spring Boot 3.3.1
- Spring Web MVC 
- gRPC
- Spring Data JPA
- Spring Data Elasticsearch
- Spring Kafka
- Spring Validation
- Spring OAuth2 Resource Server
- Hibernate 6.5.2
- Temporal SDK
- MapStruct
- Liquibase
- Lombok

---

Используемые фреймворки/библиотеки для тестирования:
- JUnit5
- Spring Boot Test
- Mockito
- Testcontainers
- H2
- Wiremock

---

Процесс аутентификации: 

![](readme-pics/auth-frontend-1.png)
![](readme-pics/auth-frontend-2.png)
![](readme-pics/auth-frontend-3.png)
![](readme-pics/auth-frontend-4.png)

---

Написан спринг бут стартер, создающий схемы индексов в Elasticsearch, если соответствующие индексы отсутствуют.

