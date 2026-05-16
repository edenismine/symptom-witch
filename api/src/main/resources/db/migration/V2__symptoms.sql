create table if not exists symptom (
    id uuid primary key,
    app_user_id uuid not null references app_user (id),
    display_name text not null,
    normalized_name text not null,
    archived boolean not null default false,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create unique index if not exists symptom_active_name_unique
    on symptom (app_user_id, normalized_name)
    where archived = false;
