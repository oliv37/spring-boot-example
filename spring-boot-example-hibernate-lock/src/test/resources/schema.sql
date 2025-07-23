create table if not exists users (
    id bigint not null,
    name varchar not null,
    active bool not null,
    version bigint not null,
    PRIMARY KEY (id),
    UNIQUE (name)
);

create sequence if not exists user_id_seq INCREMENT 50 START 1;

create table if not exists players (
    id bigint not null,
    name varchar not null,
    active bool not null,
    PRIMARY KEY (id),
    UNIQUE (name)
);

create sequence if not exists player_id_seq INCREMENT 50 START 1;
