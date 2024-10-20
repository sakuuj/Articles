CREATE TABLE persons
(
    person_id     UUID PRIMARY KEY,

    primary_email VARCHAR(50) UNIQUE NOT NULL,
    is_blocked    BOOLEAN            NOT NULL,
    created_at    TIMESTAMP          NOT NULL,
    updated_at    TIMESTAMP          NOT NULL,
    version       SMALLINT           NOT NULL
);

CREATE TABLE person_roles
(
    person_role_id SMALLINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    name    VARCHAR(50) UNIQUE NOT NULL,
    version SMALLINT           NOT NULL
);

CREATE TABLE person_to_person_role
(
    person_role_id SMALLINT NOT NULL,
    person_id      UUID     NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version    SMALLINT  NOT NULL,


    PRIMARY KEY (person_role_id, person_id),
    FOREIGN KEY (person_id)      REFERENCES persons (person_id) ON DELETE CASCADE,
    FOREIGN KEY (person_role_id) REFERENCES person_roles (person_role_id)
);

CREATE TABLE topics
(
    topic_id   UUID PRIMARY KEY,

    name       VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP          NOT NULL,
    updated_at TIMESTAMP          NOT NULL,
    version    SMALLINT           NOT NULL
);

CREATE TABLE articles
(
    article_id UUID PRIMARY KEY,

    title      VARCHAR(500) UNIQUE NOT NULL,
    content    VARCHAR(50_000)    NOT NULL,
    created_at TIMESTAMP           NOT NULL,
    updated_at TIMESTAMP           NOT NULL,
    author_id  UUID                NOT NULL,
    version    SMALLINT            NOT NULL,

    FOREIGN KEY (author_id) REFERENCES persons (person_id)
);

CREATE TABLE article_topics
(
    article_id UUID      NOT NULL,
    topic_id   UUID      NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    PRIMARY KEY (article_id, topic_id),
    FOREIGN KEY (article_id) REFERENCES articles (article_id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topics (topic_id)
);

CREATE TABLE comments
(
    comment_id UUID PRIMARY KEY,

    content    VARCHAR(5000) NOT NULL,
    created_at TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP      NOT NULL,
    version    SMALLINT       NOT NULL,
    author_id  UUID           NOT NULL,
    article_id UUID           NOT NULL,

    FOREIGN KEY (author_id) REFERENCES persons (person_id),
    FOREIGN KEY (article_id) REFERENCES articles (article_id)
);


CREATE TABLE idempotency_tokens
(
    idempotency_token UUID,
    client_id         UUID,
    creation_id       VARCHAR(50) UNIQUE NOT NULL,

    PRIMARY KEY (idempotency_token, client_id),
    FOREIGN KEY (client_id) REFERENCES persons(person_id)
);
