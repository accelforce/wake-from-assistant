CREATE TABLE configuration
(
    id            integer primary key not null,
    client_id     varchar             not null,
    client_secret varchar             not null,
    base_url      varchar             not null,
    user_id       varchar             not null,
    user_password varchar             not null,
    project_id    varchar             not null
);
