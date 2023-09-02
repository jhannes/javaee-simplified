create table todos
(
    id         uuid primary key,
    title      varchar(200) not null,
    state      varchar(20)  not null,
    created_at timestamp,
    updated_at timestamp
)