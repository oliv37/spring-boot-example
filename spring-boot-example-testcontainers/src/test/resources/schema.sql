create table if not exists players (
    id bigserial not null,
    name varchar not null,
    score bigint,
    PRIMARY KEY (id),
    UNIQUE (name)
);