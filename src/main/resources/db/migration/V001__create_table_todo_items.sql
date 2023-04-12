create table TODO_ITEMS
(
    id          uuid primary key,
    title       varchar(100) not null,
    description text,
    created_at  timestamp    not null default now(),
    updated_at  timestamp    not null default now()
)