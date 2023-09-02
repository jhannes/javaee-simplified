alter table todos add column
    created_at timestamptz not null default current_timestamp;
alter table todos add column
    created_by varchar(50) not null default 'unknown';
alter table todos add column
    updated_at timestamptz not null default current_timestamp;
alter table todos add column
    updated_by varchar(50) not null default 'unknown'
