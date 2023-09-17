create table TODOS
(
    id         uuid primary key,
    title      varchar     not null,
    status     varchar     not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);
