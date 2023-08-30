create table todos
(
    id          uuid primary key,
    title       varchar(200) not null,
    description text,
    state       varchar(30)  not null
);