DROP TABLE IF EXISTS compilation_event CASCADE;
DROP TABLE IF EXISTS event CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS compilation CASCADE;
DROP TABLE IF EXISTS participation_request CASCADE;
DROP TABLE IF EXISTS comment CASCADE;

CREATE TABLE categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name  VARCHAR(255) NOT NULL
);

CREATE TABLE location
(
    id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lat NUMERIC(10, 4) NOT NULL,
    lon NUMERIC(10, 4) NOT NULL
);

CREATE TABLE event
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    initiator_id       BIGINT       NOT NULL,
    category_id        BIGINT       NOT NULL,
    location_id        BIGINT       NOT NULL,
    event_date         TIMESTAMP,
    created_on         TIMESTAMP,
    published_on       TIMESTAMP,
    state              VARCHAR(50),
    paid               BOOLEAN,
    request_moderation BOOLEAN,
    participant_limit  INTEGER,
    confirmed_requests INTEGER,
    annotation         TEXT,
    description        TEXT,
    title              VARCHAR(255) NOT NULL,

    FOREIGN KEY (initiator_id) REFERENCES users (id),
    FOREIGN KEY (category_id) REFERENCES categories (id),
    FOREIGN KEY (location_id) REFERENCES location (id)
);

CREATE TABLE compilation
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN DEFAULT FALSE,
    title  VARCHAR(255) NOT NULL
);

CREATE TABLE compilation_event
(
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),

    FOREIGN KEY (compilation_id) REFERENCES compilation (id),
    FOREIGN KEY (event_id) REFERENCES event (id)
);

CREATE TABLE participation_request
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created_at   TIMESTAMP,
    event_id     BIGINT,
    requester_id BIGINT,
    status       VARCHAR(50),

    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT fk_requester FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE comment
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    event_id   BIGINT       NOT NULL,
    message    VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES event (id)
);