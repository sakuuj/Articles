<h1>Blogsite (backend)</h1>
<p>Backend web-приложения аналогичного <a href="https://habr.com">Хабр</a>, <a href="https://medium.com">Medium</a> </p>
<p> Используемые фреймворки/библиотеки для продакшена</p>
<ul>
    <li>Spring Boot 3.3.1</li>
    <li>Spring Web MVC</li>
    <li>gRPC</li>
    <li>Spring Data JPA</li>
    <li>Spring Data Elasticsearch</li>
    <li>Spring Validation</li>
    <li>Spring OAuth2 Resource Server</li>
    <li>Hibernate 6.5.2</li>
    <li>MapStruct</li>
    <li>Liquibase</li>
    <li>Lombok</li>
</ul>
<p> Используемые фреймворки/библиотеки для тестирования</p>
<ul>
    <li>JUnit5</li>
    <li>AssertJ</li>
    <li>Spring Boot Test</li>
    <li>Mockito</li>
    <li>Testcontainers</li>
    <li>H2</li>
    <li>Wiremock</li>
</ul>
<p>Используемые технологии</p>
<ul>
    <li>Java 21 & Gradle (Kotlin)</li>
    <li>PostgreSQL 16</li>
    <li>Elasticsearch 8</li>
</ul>

<p>Создан удобный frontend на React для получения токенов аутентификации от Google,
которые нужны для вызова некоторых HTTP-эндпоинтов. Скриншоты: 
<p align="center">
<img src="readme-pics/auth-frontend-1.png" width="800"/>
<img src="readme-pics/auth-frontend-2.png" width="800"/>
<img src="readme-pics/auth-frontend-3.png" width="800"/>
<img src="readme-pics/auth-frontend-4.png" width="800"/>
</p>
<p>Написан спринг бут стартер, создающий схемы индексов в Elasticsearch, если индексы с такими названиями отсутствуют.
(RestClient + Elasticsearch REST API) </p>

