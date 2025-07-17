DELETE
FROM compilation_event;
DELETE
FROM event;
DELETE
FROM location;
DELETE
FROM users;
DELETE
FROM categories;
DELETE
FROM compilation;
DELETE
FROM participation_request;

INSERT INTO categories (name)
VALUES ('Концерты'),
       ('Выставки'),
       ('Фестивали'),
       ('Спорт'),
       ('Образование');

INSERT INTO users (email, name)
VALUES ('ivan@mail.ru', 'Иван'),
       ('dima@mail.ru', 'Дима'),
       ('vanya@mail.ru', 'Ваня');

INSERT INTO location (lat, lon)
VALUES (55.7558, 37.6173),
       (59.9343, 30.3351),
       (51.5074, -0.1278);

INSERT INTO event (initiator_id,
                   category_id,
                   location_id,
                   event_date,
                   created_on,
                   published_on,
                   state,
                   paid,
                   request_moderation,
                   participant_limit,
                   confirmed_requests,
                   annotation,
                   description,
                   title)
VALUES (1, 1, 1, '2025-01-10 18:00:00', NOW(), NOW(),
        'PUBLISHED', TRUE, FALSE, 100, 10, 'Музыкальный концерт',
        'Большой концерт классической музыки', 'Концерт классической музыки'),
       (2, 2, 2, '2025-01-20 10:00:00', NOW(), NOW(),
        'PENDING', FALSE, TRUE, 50, 5, 'Выставка картин',
        'Выставка современных художников', 'Современная выставка'),
       (3, 3, 3, '2025-01-05 15:00:00', NOW(), NOW(),
        'CANCELED', TRUE, FALSE, 200, 20, 'Фестиваль еды',
        'Фестиваль международной кухни', 'Международный фестиваль еды'),
       (1, 1, 1, '2025-01-05 15:00:00', NOW(), NOW(),
        'PENDING', TRUE, TRUE, 202, 20, 'Фестиваль еды1',
        'Фестиваль международной кухни', 'Международный фестиваль еды'),
       (2, 2, 2, '2025-01-05 15:00:00', NOW(), NOW(),
        'PENDING', TRUE, TRUE, 201, 20, 'Фестиваль еды',
        'Фестиваль международной кухни', 'Международный фестиваль еды'),
       (3, 3, 3, '2025-01-05 15:00:00', NOW(), NOW(),
        'PENDING', TRUE, TRUE, 100, 20, 'Фестиваль еды',
        'Фестиваль международной кухни', 'Международный фестиваль еды');

INSERT INTO compilation (pinned, title)
VALUES (FALSE, 'Лучшие события декабря'),
       (TRUE, 'Мои любимые мероприятия');

INSERT INTO compilation_event (compilation_id, event_id)
VALUES (1, 1),
       (1, 2),
       (2, 3);

INSERT INTO participation_request (created_at, event_id, requester_id, status)
VALUES (NOW(), 1, 2, 'PENDING'),
       (NOW(), 2, 1, 'PENDING'),
       (NOW(), 2, 3, 'PENDING'),
       (NOW(), 3, 1, 'PENDING'),
       (NOW(), 3, 2, 'PENDING'),
       (NOW(), 1, 3, 'PENDING');