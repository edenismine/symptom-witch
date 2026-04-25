create table if not exists app_user (
    id uuid primary key,
    auth0_sub text not null unique,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
