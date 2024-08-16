CREATE TABLE persons
(
    person_id     UUID PRIMARY KEY,

    primary_email VARCHAR(50) UNIQUE NOT NULL,
    created_at    TIMESTAMP          NOT NULL,
    updated_at    TIMESTAMP          NOT NULL,
    version       SMALLINT           NOT NULL
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

    title      VARCHAR(100) UNIQUE NOT NULL,
    content    VARCHAR(1000000)    NOT NULL,
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
    FOREIGN KEY (article_id) REFERENCES articles (article_id),
    FOREIGN KEY (topic_id) REFERENCES topics (topic_id)
);

CREATE TABLE comments
(
    comment_id UUID PRIMARY KEY,

    content    VARCHAR(10000) NOT NULL,
    created_at TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP      NOT NULL,
    version    SMALLINT       NOT NULL,
    author_id  UUID           NOT NULL,
    article_id UUID           NOT NULL,

    FOREIGN KEY (author_id) REFERENCES persons (person_id),
    FOREIGN KEY (article_id) REFERENCES articles (article_id)
);
