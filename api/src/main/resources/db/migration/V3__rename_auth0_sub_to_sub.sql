alter table app_user rename column auth0_sub to sub;
alter table app_user add column version bigint not null default 0;
alter table symptom add column version bigint not null default 0;
