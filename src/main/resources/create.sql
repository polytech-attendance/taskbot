create database tasks;
drop schema if exists task_schema cascade;
create schema task_schema;

create table task_schema.owner
( owner_id integer generated always as identity primary key
, telegram_id integer
);

create table task_schema.task
( task_id integer generated always as identity primary key
, owner_id integer references owner (owner_id) on delete restrict on update restrict
, summary text
, deadline timestamp with time zone
, status integer
, estimated_time interval
, spent_time interval
);

create table task_schema.recurring_task
( recurring_task_id integer generated always as identity primary key
, owner_id integer references owner (owner_id) on delete restrict on update restrict
, summary text
, start timestamp with time zone
, period interval
, finish timestamp with time zone
, status integer
);
