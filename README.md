# Articles

---

Back-end web-приложение, содержит статьи различных тематик. Можно проводить поиск статей по тематикам, ключевым словам
имеющимся в заголовках и внутри самих статей (ключевые слова могут быть написаны с ошибками).
Изменения, относящиеся к статьям, транслируются через Kafka в Elasticsearch. Для обеспечения гарантий согласованности 
при потенциальных сбоях java-приложения, используется оркестратор микросервисов Temporal (приложение может оказаться в 
несогласованном состоянии, если не используется оркестратор и сбой сервера произошел когда изменение статьи 
уже зафиксировано в базе данных, но еще не отправлено на Kafka-топик).
При создании статей и тематик используется токен идемпотентности, а при обновлении поле version.
Для получения доступа к статьям и для их поиска по ключевым словам аутентификация не требуется.
Для публикации статей требуется аутентификация.
Регистрация и аутентификация в приложении осуществляется через аутентификацию в Google (OIDC), с помощью фронтенда по 
адресу http://localhost:8080/. Rest API адрес: http://localhost:8080/api/v1/.

Используемые технологии:
- Java 21 & Spring Boot
- Elasticsearch
- PostgreSQL
- Apache Kafka
- Temporal (Orchestrator)
- Nginx (Api-gateway)

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
- Spring-test
- Mockito
- Testcontainers
- Wiremock
- Temporal-testing

---

Написан спринг бут стартер, создающий схемы индексов в Elasticsearch, если соответствующие индексы отсутствуют.

---

## Инструкции по запуску

0. Иметь 16гб оперативной памяти, т.к. требуется запустить 10 докер контейнеров (см. [docker-compose.yml](./docker/docker-compose.yml)).
---

1. Создание jar-архивов для микросервисов текущего проекта:
`./gradlew :prepareForDocker`

---
2. Запуск контейнеров и ожидание пока они придут в готовность


```bash 
(cd ./docker && docker compose down -v && docker compose build && docker compose up) # Linux 
```
---
3. (Опционально после завершения работы) Удаление контейнеров, сетей, volume-ов: 
```bash
(cd ./docker && docker compose down -v) # Linux
```

## Процесс аутентификации: 

![](readme-pics/auth-1.png)
![](readme-pics/auth-2.png)
![](readme-pics/auth-3.png)
![](readme-pics/auth-4.png)


