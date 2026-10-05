create table workspaces (
    id uuid primary key,
    name varchar(200) not null,
    next_invoice_number integer not null default 1,
    created_at timestamptz not null default now()
);

create table users (
    id uuid primary key,
    workspace_id uuid not null references workspaces (id),
    email varchar(320) not null unique,
    password_hash varchar(100) not null,
    created_at timestamptz not null default now()
);

create table clients (
    id uuid primary key,
    workspace_id uuid not null references workspaces (id),
    name varchar(200) not null,
    email varchar(320),
    created_at timestamptz not null default now()
);
create index idx_clients_workspace on clients (workspace_id);

create table invoices (
    id uuid primary key,
    workspace_id uuid not null references workspaces (id),
    client_id uuid not null references clients (id),
    number varchar(30) not null,
    status varchar(20) not null,
    currency varchar(3) not null,
    issue_date date not null,
    due_date date not null,
    total_cents bigint not null check (total_cents >= 0),
    created_at timestamptz not null default now(),
    unique (workspace_id, number),
    check (due_date >= issue_date)
);
create index idx_invoices_workspace on invoices (workspace_id);

create table invoice_items (
    id uuid primary key,
    invoice_id uuid not null references invoices (id) on delete cascade,
    position integer not null,
    description varchar(500) not null,
    quantity integer not null check (quantity > 0),
    unit_price_cents bigint not null check (unit_price_cents >= 0)
);
create index idx_invoice_items_invoice on invoice_items (invoice_id);
